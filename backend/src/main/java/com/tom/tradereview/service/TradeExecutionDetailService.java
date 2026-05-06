package com.tom.tradereview.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tom.tradereview.entity.TradeExecutionDetail;

/**
 * 成交明细业务接口。
 *
 * <p>除 MyBatis Plus 通用 CRUD 外，额外提供会校验数据并反算交易主表的写入方法。</p>
 */
public interface TradeExecutionDetailService extends IService<TradeExecutionDetail> {
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
