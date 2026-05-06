package com.tom.tradereview.service;

import com.tom.tradereview.entity.TradeRecord;

import java.time.LocalDate;
import java.util.List;

/**
 * 交易记录业务接口。
 *
 * <p>这里暴露的是“用户可编辑的交易基础信息”操作；交易汇总字段由成交明细服务维护。</p>
 */
public interface TradeRecordService {
    List<TradeRecord> list(LocalDate startDate, LocalDate endDate, String stockName, Integer isPatternTrade);

    List<TradeRecord> listByTradeDateRange(LocalDate startDate, LocalDate endDate);

    TradeRecord getById(Long id);

    /**
     * 创建交易基础信息。
     */
    TradeRecord createTrade(TradeRecord tradeRecord);

    /**
     * 更新交易基础信息。
     */
    TradeRecord updateTrade(Long id, TradeRecord tradeRecord);

    /**
     * 删除交易及其关联数据。
     */
    boolean deleteTrade(Long id);

    /**
     * 更新由成交明细反算出的交易汇总字段。
     */
    boolean updateSummary(TradeRecord tradeRecord);
}
