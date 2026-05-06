package com.tom.tradereview.service.impl;

import com.tom.tradereview.entity.TradeReview;
import com.tom.tradereview.mapper.TradeReviewMapper;
import com.tom.tradereview.service.TradeReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 单笔交易复盘服务实现。
 */
@Service
@RequiredArgsConstructor
public class TradeReviewServiceImpl implements TradeReviewService {
    private final TradeReviewMapper tradeReviewMapper;

    @Override
    public boolean save(TradeReview tradeReview) {
        return tradeReviewMapper.insert(tradeReview) > 0;
    }

    @Override
    public TradeReview getByTradeId(Long tradeId) {
        return tradeReviewMapper.selectByTradeId(tradeId);
    }

    @Override
    public boolean updateById(TradeReview tradeReview) {
        return tradeReviewMapper.updateById(tradeReview) > 0;
    }

    @Override
    public TradeReview getById(Long id) {
        return tradeReviewMapper.selectById(id);
    }
}
