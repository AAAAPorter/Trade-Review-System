package com.tom.tradereview.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tom.tradereview.entity.TradeRecord;
import com.tom.tradereview.service.TradeRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trades")
public class TradeRecordController {
    private final TradeRecordService tradeRecordService;

    @GetMapping
    public List<TradeRecord> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String stockName,
            @RequestParam(required = false) Integer isPatternTrade
    ) {
        LambdaQueryWrapper<TradeRecord> query = new LambdaQueryWrapper<TradeRecord>()
                .ge(startDate != null, TradeRecord::getTradeDate, startDate)
                .le(endDate != null, TradeRecord::getTradeDate, endDate)
                .like(stockName != null && !stockName.isBlank(), TradeRecord::getStockName, stockName)
                .eq(isPatternTrade != null, TradeRecord::getIsPatternTrade, isPatternTrade)
                .orderByDesc(TradeRecord::getTradeDate)
                .orderByDesc(TradeRecord::getId);
        return tradeRecordService.list(query);
    }

    @GetMapping("/{id}")
    public TradeRecord detail(@PathVariable Long id) {
        return tradeRecordService.getById(id);
    }

    @PostMapping
    public TradeRecord create(@RequestBody TradeRecord tradeRecord) {
        tradeRecordService.save(tradeRecord);
        return tradeRecord;
    }

    @PutMapping("/{id}")
    public TradeRecord update(@PathVariable Long id, @RequestBody TradeRecord tradeRecord) {
        tradeRecord.setId(id);
        tradeRecordService.updateById(tradeRecord);
        return tradeRecordService.getById(id);
    }

    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Long id) {
        return tradeRecordService.removeById(id);
    }
}
