package com.tom.tradereview.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tom.tradereview.entity.TradeExecutionDetail;

public interface TradeExecutionDetailService extends IService<TradeExecutionDetail> {
    TradeExecutionDetail createForTrade(Long tradeId, TradeExecutionDetail detail);

    void createBatchForTrade(Long tradeId, java.util.List<TradeExecutionDetail> details);

    TradeExecutionDetail updateDetail(Long id, TradeExecutionDetail detail);

    boolean deleteDetail(Long id);
}
