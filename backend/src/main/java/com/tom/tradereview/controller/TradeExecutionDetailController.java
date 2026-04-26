package com.tom.tradereview.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tom.tradereview.entity.TradeExecutionDetail;
import com.tom.tradereview.service.TradeExecutionDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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
public class TradeExecutionDetailController {
    private final TradeExecutionDetailService tradeExecutionDetailService;

    @GetMapping("/api/trades/{tradeId}/execution-details")
    public List<TradeExecutionDetail> list(@PathVariable Long tradeId) {
        return tradeExecutionDetailService.list(new LambdaQueryWrapper<TradeExecutionDetail>()
                .eq(TradeExecutionDetail::getTradeId, tradeId)
                .orderByAsc(TradeExecutionDetail::getExecutionTime)
                .orderByAsc(TradeExecutionDetail::getId));
    }

    @PostMapping("/api/trades/{tradeId}/execution-details")
    public TradeExecutionDetail create(@PathVariable Long tradeId, @RequestBody TradeExecutionDetail detail) {
        return tradeExecutionDetailService.createForTrade(tradeId, detail);
    }

    @PutMapping("/api/trade-execution-details/{id}")
    public TradeExecutionDetail update(@PathVariable Long id, @RequestBody TradeExecutionDetail detail) {
        return tradeExecutionDetailService.updateDetail(id, detail);
    }

    @DeleteMapping("/api/trade-execution-details/{id}")
    public Boolean delete(@PathVariable Long id) {
        return tradeExecutionDetailService.deleteDetail(id);
    }
}
