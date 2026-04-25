package com.tom.tradereview.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tom.tradereview.entity.WeeklyReview;
import com.tom.tradereview.service.WeeklyReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/weekly-reviews")
public class WeeklyReviewController {
    private final WeeklyReviewService weeklyReviewService;

    @GetMapping
    public List<WeeklyReview> list() {
        return weeklyReviewService.list(new LambdaQueryWrapper<WeeklyReview>().orderByDesc(WeeklyReview::getWeekStart));
    }

    @GetMapping("/{id}")
    public WeeklyReview detail(@PathVariable Long id) {
        return weeklyReviewService.getById(id);
    }

    @PostMapping
    public WeeklyReview create(@RequestBody WeeklyReview weeklyReview) {
        weeklyReviewService.save(weeklyReview);
        return weeklyReview;
    }

    @PutMapping("/{id}")
    public WeeklyReview update(@PathVariable Long id, @RequestBody WeeklyReview weeklyReview) {
        weeklyReview.setId(id);
        weeklyReviewService.updateById(weeklyReview);
        return weeklyReviewService.getById(id);
    }
}
