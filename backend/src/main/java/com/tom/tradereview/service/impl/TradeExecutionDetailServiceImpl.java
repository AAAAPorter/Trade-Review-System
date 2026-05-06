package com.tom.tradereview.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tom.tradereview.entity.TradeExecutionDetail;
import com.tom.tradereview.entity.TradeRecord;
import com.tom.tradereview.mapper.TradeExecutionDetailMapper;
import com.tom.tradereview.service.TradeExecutionDetailService;
import com.tom.tradereview.service.TradeRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 成交明细服务实现。
 *
 * <p>这是系统里最关键的业务服务：每次明细变化后，都会重新聚合所属交易的买卖均价、累计数量、
 * 剩余数量、持仓状态、已实现盈亏和收益率。这样交易列表、详情、周统计都只需要读取 trade_record。</p>
 */
@Service
@RequiredArgsConstructor
public class TradeExecutionDetailServiceImpl
        extends ServiceImpl<TradeExecutionDetailMapper, TradeExecutionDetail>
        implements TradeExecutionDetailService {

    private static final String BUY = "BUY";
    private static final String SELL = "SELL";

    private final TradeRecordService tradeRecordService;

    /**
     * 新增单条成交明细：先校验交易存在，再校验明细合法性和卖出数量约束。
     */
    @Override
    @Transactional
    public TradeExecutionDetail createForTrade(Long tradeId, TradeExecutionDetail detail) {
        ensureTradeExists(tradeId);
        detail.setTradeId(tradeId);
        validateDetail(detail);
        List<TradeExecutionDetail> details = detailsForTrade(tradeId);
        details.add(detail);
        validateSellQuantity(details);
        save(detail);
        recalculateTradeSummary(tradeId);
        return detail;
    }

    /**
     * 批量新增明细，通常用于新增交易时把前端草稿一次性落库。
     */
    @Override
    @Transactional
    public void createBatchForTrade(Long tradeId, List<TradeExecutionDetail> details) {
        ensureTradeExists(tradeId);
        if (details == null || details.isEmpty()) {
            recalculateTradeSummary(tradeId);
            return;
        }
        details.forEach(detail -> {
            detail.setTradeId(tradeId);
            validateDetail(detail);
        });
        List<TradeExecutionDetail> combinedDetails = new ArrayList<>(detailsForTrade(tradeId));
        combinedDetails.addAll(details);
        validateSellQuantity(combinedDetails);
        saveBatch(details);
        recalculateTradeSummary(tradeId);
    }

    /**
     * 编辑明细时先用新明细替换旧明细参与校验，避免卖出数量超过买入数量。
     */
    @Override
    @Transactional
    public TradeExecutionDetail updateDetail(Long id, TradeExecutionDetail detail) {
        TradeExecutionDetail oldDetail = getRequiredDetail(id);
        detail.setId(id);
        detail.setTradeId(oldDetail.getTradeId());
        validateDetail(detail);

        List<TradeExecutionDetail> details = detailsForTrade(oldDetail.getTradeId()).stream()
                .filter(item -> !Objects.equals(item.getId(), id))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        details.add(detail);
        validateSellQuantity(details);

        updateById(detail);
        recalculateTradeSummary(oldDetail.getTradeId());
        return getById(id);
    }

    /**
     * 删除明细后重新计算交易汇总，保持主表派生字段与明细一致。
     */
    @Override
    @Transactional
    public boolean deleteDetail(Long id) {
        TradeExecutionDetail detail = getRequiredDetail(id);
        boolean removed = removeById(id);
        recalculateTradeSummary(detail.getTradeId());
        return removed;
    }

    /**
     * 从成交明细反算交易主表的汇总字段。
     *
     * <p>如果已经没有明细，则把所有派生字段清空；否则按 BUY/SELL 分别计算均价和数量，
     * 再根据是否清仓/部分平仓决定是否计算已实现盈亏。</p>
     */
    private void recalculateTradeSummary(Long tradeId) {
        List<TradeExecutionDetail> details = detailsForTrade(tradeId);
        int totalBuyQuantity = sumQuantity(details, BUY);
        int totalSellQuantity = sumQuantity(details, SELL);
        int remainingQuantity = totalBuyQuantity - totalSellQuantity;

        if (details.isEmpty()) {
            tradeRecordService.update(new LambdaUpdateWrapper<TradeRecord>()
                    .eq(TradeRecord::getId, tradeId)
                    .set(TradeRecord::getBuyTime, null)
                    .set(TradeRecord::getBuyPrice, null)
                    .set(TradeRecord::getSellTime, null)
                    .set(TradeRecord::getSellPrice, null)
                    .set(TradeRecord::getTradeDate, null)
                    .set(TradeRecord::getTotalBuyQuantity, null)
                    .set(TradeRecord::getTotalSellQuantity, null)
                    .set(TradeRecord::getRemainingQuantity, null)
                    .set(TradeRecord::getAvgBuyPrice, null)
                    .set(TradeRecord::getAvgSellPrice, null)
                    .set(TradeRecord::getPositionStatus, null)
                    .set(TradeRecord::getProfitAmount, null)
                    .set(TradeRecord::getProfitRate, null));
            return;
        }

        BigDecimal avgBuyPrice = avgPrice(details, BUY, totalBuyQuantity);
        BigDecimal avgSellPrice = avgPrice(details, SELL, totalSellQuantity);
        LocalDateTime firstBuyTime = firstExecutionTime(details, BUY);
        LocalDateTime lastSellTime = lastExecutionTime(details, SELL);
        String positionStatus = positionStatus(totalSellQuantity, remainingQuantity);
        BigDecimal totalBuyAmount = totalAmount(details, BUY);
        BigDecimal totalSellAmount = totalAmount(details, SELL);
        BigDecimal profitAmount = profitAmount(positionStatus, totalBuyAmount, totalSellAmount, avgBuyPrice, totalSellQuantity);
        BigDecimal profitRate = profitRate(positionStatus, profitAmount, totalBuyAmount, avgBuyPrice, totalSellQuantity);

        tradeRecordService.update(new LambdaUpdateWrapper<TradeRecord>()
                .eq(TradeRecord::getId, tradeId)
                .set(TradeRecord::getBuyTime, firstBuyTime)
                .set(TradeRecord::getBuyPrice, avgBuyPrice)
                .set(TradeRecord::getSellTime, lastSellTime)
                .set(TradeRecord::getSellPrice, avgSellPrice)
                .set(TradeRecord::getTradeDate, lastSellTime != null ? lastSellTime.toLocalDate() : firstBuyTime == null ? null : firstBuyTime.toLocalDate())
                .set(TradeRecord::getTotalBuyQuantity, totalBuyQuantity)
                .set(TradeRecord::getTotalSellQuantity, totalSellQuantity)
                .set(TradeRecord::getRemainingQuantity, remainingQuantity)
                .set(TradeRecord::getAvgBuyPrice, avgBuyPrice)
                .set(TradeRecord::getAvgSellPrice, avgSellPrice)
                .set(TradeRecord::getPositionStatus, positionStatus)
                .set(TradeRecord::getProfitAmount, profitAmount)
                .set(TradeRecord::getProfitRate, profitRate));
    }

    /**
     * 根据剩余数量判断持仓状态。
     */
    private String positionStatus(int totalSellQuantity, int remainingQuantity) {
        if (remainingQuantity > 0 && totalSellQuantity == 0) {
            return "OPEN";
        }
        if (remainingQuantity > 0) {
            return "PARTIAL_CLOSED";
        }
        if (remainingQuantity == 0 && totalSellQuantity > 0) {
            return "CLOSED";
        }
        return null;
    }

    /**
     * 加权平均价 = 该方向成交金额合计 / 该方向成交数量合计。
     */
    private BigDecimal avgPrice(List<TradeExecutionDetail> details, String actionType, int quantity) {
        if (quantity == 0) {
            return null;
        }
        BigDecimal amount = totalAmount(details, actionType);
        return amount.divide(BigDecimal.valueOf(quantity), 3, RoundingMode.HALF_UP);
    }

    /**
     * 计算某个方向的成交金额合计。
     */
    private BigDecimal totalAmount(List<TradeExecutionDetail> details, String actionType) {
        return details.stream()
                .filter(item -> actionType.equals(item.getActionType()))
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 计算已实现盈亏。
     *
     * <p>清仓时用总卖出金额 - 总买入金额；部分平仓时只计算已卖出数量对应的买入成本。</p>
     */
    private BigDecimal profitAmount(
            String positionStatus,
            BigDecimal totalBuyAmount,
            BigDecimal totalSellAmount,
            BigDecimal avgBuyPrice,
            int totalSellQuantity
    ) {
        if (totalBuyAmount == null || totalBuyAmount.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        BigDecimal profit;
        if ("CLOSED".equals(positionStatus)) {
            profit = totalSellAmount.subtract(totalBuyAmount);
        } else if ("PARTIAL_CLOSED".equals(positionStatus) && avgBuyPrice != null && totalSellQuantity > 0) {
            profit = totalSellAmount.subtract(avgBuyPrice.multiply(BigDecimal.valueOf(totalSellQuantity)));
        } else {
            return null;
        }
        return profit.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 计算已实现收益率，分母与 profitAmount 的成本口径保持一致。
     */
    private BigDecimal profitRate(
            String positionStatus,
            BigDecimal profitAmount,
            BigDecimal totalBuyAmount,
            BigDecimal avgBuyPrice,
            int totalSellQuantity
    ) {
        if (profitAmount == null) {
            return null;
        }
        BigDecimal denominator;
        if ("CLOSED".equals(positionStatus)) {
            denominator = totalBuyAmount;
        } else if ("PARTIAL_CLOSED".equals(positionStatus) && avgBuyPrice != null && totalSellQuantity > 0) {
            denominator = avgBuyPrice.multiply(BigDecimal.valueOf(totalSellQuantity));
        } else {
            return null;
        }
        if (denominator == null || denominator.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return profitAmount.divide(denominator, 4, RoundingMode.HALF_UP);
    }

    /**
     * 获取某方向最早成交时间。
     */
    private LocalDateTime firstExecutionTime(List<TradeExecutionDetail> details, String actionType) {
        return details.stream()
                .filter(item -> actionType.equals(item.getActionType()))
                .map(TradeExecutionDetail::getExecutionTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    /**
     * 获取某方向最晚成交时间。
     */
    private LocalDateTime lastExecutionTime(List<TradeExecutionDetail> details, String actionType) {
        return details.stream()
                .filter(item -> actionType.equals(item.getActionType()))
                .map(TradeExecutionDetail::getExecutionTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    /**
     * 汇总某方向的成交数量。
     */
    private int sumQuantity(List<TradeExecutionDetail> details, String actionType) {
        return details.stream()
                .filter(item -> actionType.equals(item.getActionType()))
                .map(TradeExecutionDetail::getQuantity)
                .filter(Objects::nonNull)
                .reduce(0, Integer::sum);
    }

    /**
     * 单条明细的基础业务校验。
     */
    private void validateDetail(TradeExecutionDetail detail) {
        if (!BUY.equals(detail.getActionType()) && !SELL.equals(detail.getActionType())) {
            throw badRequest("actionType 只允许 BUY 或 SELL");
        }
        if (detail.getExecutionTime() == null) {
            throw badRequest("executionTime 不能为空");
        }
        if (detail.getPrice() == null || detail.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw badRequest("price 必须大于 0");
        }
        if (detail.getQuantity() == null || detail.getQuantity() <= 0) {
            throw badRequest("quantity 必须大于 0");
        }
    }

    /**
     * 防止出现“卖出数量大于买入数量”的非法持仓。
     */
    private void validateSellQuantity(List<TradeExecutionDetail> details) {
        int totalBuyQuantity = sumQuantity(details, BUY);
        int totalSellQuantity = sumQuantity(details, SELL);
        if (totalSellQuantity > totalBuyQuantity) {
            throw badRequest("卖出总数量不能大于买入总数量");
        }
    }

    /**
     * 查询某笔交易的全部成交明细。
     */
    private List<TradeExecutionDetail> detailsForTrade(Long tradeId) {
        return list(new LambdaQueryWrapper<TradeExecutionDetail>()
                .eq(TradeExecutionDetail::getTradeId, tradeId));
    }

    /**
     * 写入明细前必须确认交易主表存在。
     */
    private void ensureTradeExists(Long tradeId) {
        if (tradeId == null || tradeRecordService.getById(tradeId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "交易记录不存在");
        }
    }

    /**
     * 编辑或删除明细时使用，找不到直接返回 404。
     */
    private TradeExecutionDetail getRequiredDetail(Long id) {
        TradeExecutionDetail detail = getById(id);
        if (detail == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "成交明细不存在");
        }
        return detail;
    }

    /**
     * 统一构造 400 业务异常。
     */
    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
