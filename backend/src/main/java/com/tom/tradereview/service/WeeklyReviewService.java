package com.tom.tradereview.service;

import com.tom.tradereview.entity.WeeklyReview;

import java.util.List;

/**
 * 周复盘服务。
 */
public interface WeeklyReviewService {
    List<WeeklyReview> listOrderByWeekStartDesc();

    WeeklyReview getById(Long id);

    boolean save(WeeklyReview weeklyReview);

    boolean updateById(WeeklyReview weeklyReview);

    WeeklyReview latest();
}
