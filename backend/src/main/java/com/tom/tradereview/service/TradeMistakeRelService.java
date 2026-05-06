package com.tom.tradereview.service;

import com.tom.tradereview.entity.TradeMistakeRel;

import java.util.List;

/**
 * 交易-错误标签关系服务。
 */
public interface TradeMistakeRelService {
    List<TradeMistakeRel> listByTradeId(Long tradeId);

    List<TradeMistakeRel> listByTradeIds(List<Long> tradeIds);

    boolean removeByTradeId(Long tradeId);

    boolean removeByMistakeTagId(Long mistakeTagId);

    boolean saveBatch(List<TradeMistakeRel> relations);
}
