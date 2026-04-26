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
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TradeExecutionDetailServiceImpl
        extends ServiceImpl<TradeExecutionDetailMapper, TradeExecutionDetail>
        implements TradeExecutionDetailService {

    private static final String BUY = "BUY";
    private static final String SELL = "SELL";

    private final TradeRecordService tradeRecordService;

    @Override
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

    @Override
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
        validateSellQuantity(details);
        saveBatch(details);
        recalculateTradeSummary(tradeId);
    }

    @Override
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

    @Override
    public boolean deleteDetail(Long id) {
        TradeExecutionDetail detail = getRequiredDetail(id);
        boolean removed = removeById(id);
        recalculateTradeSummary(detail.getTradeId());
        return removed;
    }

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

    private BigDecimal avgPrice(List<TradeExecutionDetail> details, String actionType, int quantity) {
        if (quantity == 0) {
            return null;
        }
        BigDecimal amount = totalAmount(details, actionType);
        return amount.divide(BigDecimal.valueOf(quantity), 3, RoundingMode.HALF_UP);
    }

    private BigDecimal totalAmount(List<TradeExecutionDetail> details, String actionType) {
        return details.stream()
                .filter(item -> actionType.equals(item.getActionType()))
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

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

    private LocalDateTime firstExecutionTime(List<TradeExecutionDetail> details, String actionType) {
        return details.stream()
                .filter(item -> actionType.equals(item.getActionType()))
                .map(TradeExecutionDetail::getExecutionTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    private LocalDateTime lastExecutionTime(List<TradeExecutionDetail> details, String actionType) {
        return details.stream()
                .filter(item -> actionType.equals(item.getActionType()))
                .map(TradeExecutionDetail::getExecutionTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    private int sumQuantity(List<TradeExecutionDetail> details, String actionType) {
        return details.stream()
                .filter(item -> actionType.equals(item.getActionType()))
                .map(TradeExecutionDetail::getQuantity)
                .filter(Objects::nonNull)
                .reduce(0, Integer::sum);
    }

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

    private void validateSellQuantity(List<TradeExecutionDetail> details) {
        int totalBuyQuantity = sumQuantity(details, BUY);
        int totalSellQuantity = sumQuantity(details, SELL);
        if (totalSellQuantity > totalBuyQuantity) {
            throw badRequest("卖出总数量不能大于买入总数量");
        }
    }

    private List<TradeExecutionDetail> detailsForTrade(Long tradeId) {
        return list(new LambdaQueryWrapper<TradeExecutionDetail>()
                .eq(TradeExecutionDetail::getTradeId, tradeId));
    }

    private void ensureTradeExists(Long tradeId) {
        if (tradeId == null || tradeRecordService.getById(tradeId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "交易记录不存在");
        }
    }

    private TradeExecutionDetail getRequiredDetail(Long id) {
        TradeExecutionDetail detail = getById(id);
        if (detail == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "成交明细不存在");
        }
        return detail;
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
