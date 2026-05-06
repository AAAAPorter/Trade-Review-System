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

/**
 * 成交明细接口。
 *
 * <p>成交明细是交易记录的计算来源：新增、编辑、删除后都会触发主交易的均价、盈亏、
 * 持仓状态等汇总字段重新计算。</p>
 */
@CrossOrigin
@RestController
@RequiredArgsConstructor
public class TradeExecutionDetailController {
    private final TradeExecutionDetailService tradeExecutionDetailService;

    /**
     * 按成交时间排序，便于前端还原真实操作顺序。
     */
    @GetMapping("/api/trades/{tradeId}/execution-details")
    public List<TradeExecutionDetail> list(@PathVariable Long tradeId) {
        return tradeExecutionDetailService.list(new LambdaQueryWrapper<TradeExecutionDetail>()
                .eq(TradeExecutionDetail::getTradeId, tradeId)
                .orderByAsc(TradeExecutionDetail::getExecutionTime)
                .orderByAsc(TradeExecutionDetail::getId));
    }

    /**
     * 给指定交易追加一条买入或卖出明细。
     */
    @PostMapping("/api/trades/{tradeId}/execution-details")
    public TradeExecutionDetail create(@PathVariable Long tradeId, @RequestBody TradeExecutionDetail detail) {
        return tradeExecutionDetailService.createForTrade(tradeId, detail);
    }

    /**
     * 明细自身有独立 id，因此编辑接口挂在 /trade-execution-details/{id} 下。
     */
    @PutMapping("/api/trade-execution-details/{id}")
    public TradeExecutionDetail update(@PathVariable Long id, @RequestBody TradeExecutionDetail detail) {
        return tradeExecutionDetailService.updateDetail(id, detail);
    }

    /**
     * 删除后同样需要反算交易主表，逻辑收口在 Service 层。
     */
    @DeleteMapping("/api/trade-execution-details/{id}")
    public Boolean delete(@PathVariable Long id) {
        return tradeExecutionDetailService.deleteDetail(id);
    }
}
