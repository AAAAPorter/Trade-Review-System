package com.tom.tradereview.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tom.tradereview.entity.WeeklyReview;
import com.tom.tradereview.mapper.WeeklyReviewMapper;
import com.tom.tradereview.service.WeeklyReviewService;
import org.springframework.stereotype.Service;

@Service
public class WeeklyReviewServiceImpl extends ServiceImpl<WeeklyReviewMapper, WeeklyReview> implements WeeklyReviewService {
}
