package com.tom.tradereview.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tom.tradereview.entity.WeeklyReview;
import com.tom.tradereview.service.WeeklyReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rule-card")
public class RuleCardController {
    private final WeeklyReviewService weeklyReviewService;

    @GetMapping
    public WeeklyReview latest() {
        return weeklyReviewService.getOne(
                new LambdaQueryWrapper<WeeklyReview>().orderByDesc(WeeklyReview::getWeekStart).last("limit 1"),
                false
        );
    }
}
