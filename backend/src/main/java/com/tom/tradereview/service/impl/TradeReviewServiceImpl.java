package com.tom.tradereview.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tom.tradereview.entity.TradeReview;
import com.tom.tradereview.mapper.TradeReviewMapper;
import com.tom.tradereview.service.TradeReviewService;
import org.springframework.stereotype.Service;

@Service
public class TradeReviewServiceImpl extends ServiceImpl<TradeReviewMapper, TradeReview> implements TradeReviewService {
}
