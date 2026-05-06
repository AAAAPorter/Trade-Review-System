package com.tom.tradereview.service.impl;

import com.tom.tradereview.entity.WeeklyReview;
import com.tom.tradereview.mapper.WeeklyReviewMapper;
import com.tom.tradereview.service.WeeklyReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 周复盘服务实现。
 */
@Service
@RequiredArgsConstructor
public class WeeklyReviewServiceImpl implements WeeklyReviewService {
    private final WeeklyReviewMapper weeklyReviewMapper;

    @Override
    public List<WeeklyReview> listOrderByWeekStartDesc() {
        return weeklyReviewMapper.selectAllOrderByWeekStartDesc();
    }

    @Override
    public WeeklyReview getById(Long id) {
        return weeklyReviewMapper.selectById(id);
    }

    @Override
    public boolean save(WeeklyReview weeklyReview) {
        return weeklyReviewMapper.insert(weeklyReview) > 0;
    }

    @Override
    public boolean updateById(WeeklyReview weeklyReview) {
        return weeklyReviewMapper.updateById(weeklyReview) > 0;
    }

    @Override
    public WeeklyReview latest() {
        return weeklyReviewMapper.selectLatest();
    }
}
