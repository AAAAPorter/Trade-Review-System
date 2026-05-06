package com.tom.tradereview.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tom.tradereview.entity.WeeklyReview;
import com.tom.tradereview.service.WeeklyReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 纪律卡片接口。
 *
 * <p>页面只需要最近一份周复盘中的三条纪律和训练主题，所以这里直接返回最新周复盘。</p>
 */
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rule-card")
public class RuleCardController {
    private final WeeklyReviewService weeklyReviewService;

    /**
     * MySQL 的 limit 1 放在 wrapper.last 中，避免多取数据再在 Java 内存里裁剪。
     */
    @GetMapping
    public WeeklyReview latest() {
        return weeklyReviewService.getOne(
                new LambdaQueryWrapper<WeeklyReview>().orderByDesc(WeeklyReview::getWeekStart).last("limit 1"),
                false
        );
    }
}
