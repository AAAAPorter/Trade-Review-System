package com.tom.tradereview.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tom.tradereview.dto.TradeWithExecutionDetailsDTO;
import com.tom.tradereview.entity.TradeMistakeRel;
import com.tom.tradereview.entity.TradeRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeBundleService {
    private final TradeRecordService tradeRecordService;
    private final TradeMistakeRelService tradeMistakeRelService;
    private final TradeExecutionDetailService tradeExecutionDetailService;

    @Transactional
    public TradeRecord createWithExecutionDetails(TradeWithExecutionDetailsDTO dto) {
        TradeRecord tradeRecord = dto.getTradeRecord() == null ? new TradeRecord() : dto.getTradeRecord();
        tradeRecordService.save(tradeRecord);
        replaceMistakes(tradeRecord.getId(), dto.getMistakeTagIds());
        tradeExecutionDetailService.createBatchForTrade(tradeRecord.getId(), dto.getExecutionDetails());
        return tradeRecordService.getById(tradeRecord.getId());
    }

    private void replaceMistakes(Long tradeId, List<Long> mistakeTagIds) {
        tradeMistakeRelService.remove(new LambdaQueryWrapper<TradeMistakeRel>().eq(TradeMistakeRel::getTradeId, tradeId));
        if (mistakeTagIds == null || mistakeTagIds.isEmpty()) {
            return;
        }
        tradeMistakeRelService.saveBatch(mistakeTagIds.stream().map(tagId -> {
            TradeMistakeRel rel = new TradeMistakeRel();
            rel.setTradeId(tradeId);
            rel.setMistakeTagId(tagId);
            return rel;
        }).toList());
    }
}
