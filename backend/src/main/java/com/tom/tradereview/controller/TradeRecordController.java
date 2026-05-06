package com.tom.tradereview.controller;

import com.tom.tradereview.dto.TradeWithExecutionDetailsDTO;
import com.tom.tradereview.entity.TradeRecord;
import com.tom.tradereview.service.TradeBundleService;
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

/**
 * 交易记录接口。
 *
 * <p>这里负责“交易主表”的查询和基础 CRUD；涉及成交明细、错误标签一起创建的复合流程，
 * 会交给 TradeBundleService 保证事务一致性。</p>
 */
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trades")
public class TradeRecordController {
    private final TradeRecordService tradeRecordService;
    private final TradeBundleService tradeBundleService;

    /**
     * 交易列表支持按统计归属日期、股票名称、模式内外过滤。
     *
     * <p>空筛选项会以 null 传给 Mapper，由 MyBatis 动态 SQL 决定是否拼接条件。</p>
     */
    @GetMapping
    public List<TradeRecord> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String stockName,
            @RequestParam(required = false) Integer isPatternTrade
    ) {
        return tradeRecordService.list(startDate, endDate, stockName, isPatternTrade);
    }

    @GetMapping("/{id}")
    public TradeRecord detail(@PathVariable Long id) {
        return tradeRecordService.getById(id);
    }

    /**
     * 只创建交易基础信息；成交明细可在后续通过 /execution-details 接口逐条维护。
     */
    @PostMapping
    public TradeRecord create(@RequestBody TradeRecord tradeRecord) {
        return tradeRecordService.createTrade(tradeRecord);
    }

    /**
     * 新增交易页的一次性保存入口：基础信息、错误标签、草稿成交明细一起落库。
     */
    @PostMapping("/with-execution-details")
    public TradeRecord createWithExecutionDetails(@RequestBody TradeWithExecutionDetailsDTO dto) {
        return tradeBundleService.createWithExecutionDetails(dto);
    }

    /**
     * 只更新用户可编辑的交易基础信息；系统汇总字段由成交明细服务反算。
     */
    @PutMapping("/{id}")
    public TradeRecord update(@PathVariable Long id, @RequestBody TradeRecord tradeRecord) {
        return tradeRecordService.updateTrade(id, tradeRecord);
    }

    /**
     * 删除交易时，Service 会同步清理成交明细、错误标签关系和单笔复盘。
     */
    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Long id) {
        return tradeRecordService.deleteTrade(id);
    }
}
