package com.tom.tradereview.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tom.tradereview.entity.TradeRecord;

public interface TradeRecordService extends IService<TradeRecord> {
    TradeRecord createTrade(TradeRecord tradeRecord);

    TradeRecord updateTrade(Long id, TradeRecord tradeRecord);

    boolean deleteTrade(Long id);
}
