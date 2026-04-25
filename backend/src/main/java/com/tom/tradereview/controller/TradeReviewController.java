package com.tom.tradereview.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tom.tradereview.entity.TradeReview;
import com.tom.tradereview.service.TradeReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trade-reviews")
public class TradeReviewController {
    private final TradeReviewService tradeReviewService;

    @PostMapping
    public TradeReview create(@RequestBody TradeReview tradeReview) {
        tradeReviewService.save(tradeReview);
        return tradeReview;
    }

    @GetMapping("/{tradeId}")
    public TradeReview detailByTradeId(@PathVariable Long tradeId) {
        return tradeReviewService.getOne(new LambdaQueryWrapper<TradeReview>().eq(TradeReview::getTradeId, tradeId), false);
    }

    @PutMapping("/{id}")
    public TradeReview update(@PathVariable Long id, @RequestBody TradeReview tradeReview) {
        tradeReview.setId(id);
        tradeReviewService.updateById(tradeReview);
        return tradeReviewService.getById(id);
    }
}
