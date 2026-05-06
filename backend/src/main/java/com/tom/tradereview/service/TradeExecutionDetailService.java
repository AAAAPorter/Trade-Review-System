package com.tom.tradereview.service;

import com.tom.tradereview.entity.TradeExecutionDetail;

import java.util.List;

/**
 * 成交明细业务接口。
 *
 * <p>这里提供会校验数据并反算交易主表的写入方法。</p>
 */
public interface TradeExecutionDetailService {
    /**
     * 查询指定交易的成交明细，按成交时间排序。
     */
    List<TradeExecutionDetail> listByTradeIdOrderByExecutionTime(Long tradeId);

    /**
     * 为指定交易新增一条成交明细。
     */
    TradeExecutionDetail createForTrade(Long tradeId, TradeExecutionDetail detail);

    /**
     * 批量创建成交明细，主要用于新增交易时一次性保存草稿明细。
     */
    void createBatchForTrade(Long tradeId, java.util.List<TradeExecutionDetail> details);

    /**
     * 编辑成交明细，并同步反算所属交易。
     */
    TradeExecutionDetail updateDetail(Long id, TradeExecutionDetail detail);

    /**
     * 删除成交明细，并同步反算所属交易。
     */
    boolean deleteDetail(Long id);
}
