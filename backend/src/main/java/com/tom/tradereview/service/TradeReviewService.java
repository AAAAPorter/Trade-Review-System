package com.tom.tradereview.service;

import com.tom.tradereview.entity.TradeReview;

/**
 * 单笔交易复盘服务。
 */
public interface TradeReviewService {
    boolean save(TradeReview tradeReview);

    TradeReview getByTradeId(Long tradeId);

    boolean updateById(TradeReview tradeReview);

    TradeReview getById(Long id);
}
