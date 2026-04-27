package com.tom.tradereview.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tom.tradereview.dto.TradeWithExecutionDetailsDTO;
import com.tom.tradereview.entity.TradeMistakeRel;
import com.tom.tradereview.entity.TradeRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeBundleService {
    private final TradeRecordService tradeRecordService;
    private final TradeMistakeRelService tradeMistakeRelService;
    private final TradeExecutionDetailService tradeExecutionDetailService;

    @Transactional
    public TradeRecord createWithExecutionDetails(TradeWithExecutionDetailsDTO dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "交易保存数据不能为空");
        }
        TradeRecord tradeRecord = tradeRecordService.createTrade(dto.getTradeRecord());
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
