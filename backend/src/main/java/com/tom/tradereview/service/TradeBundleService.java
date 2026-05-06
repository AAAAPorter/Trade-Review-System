package com.tom.tradereview.service;

import com.tom.tradereview.dto.TradeWithExecutionDetailsDTO;
import com.tom.tradereview.entity.TradeMistakeRel;
import com.tom.tradereview.entity.TradeRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * 交易组合保存服务。
 *
 * <p>新增交易页会同时提交交易基础信息、错误标签和成交明细草稿。这个服务把多张表写入放进
 * 同一个事务，避免只保存了一部分数据。</p>
 */
@Service
@RequiredArgsConstructor
public class TradeBundleService {
    private final TradeRecordService tradeRecordService;
    private final TradeMistakeRelService tradeMistakeRelService;
    private final TradeExecutionDetailService tradeExecutionDetailService;

    /**
     * 一次性创建交易及其关联数据。
     */
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

    /**
     * 交易标签采用替换语义：先移除旧关系，再保存本次提交的完整标签集合。
     */
    private void replaceMistakes(Long tradeId, List<Long> mistakeTagIds) {
        tradeMistakeRelService.removeByTradeId(tradeId);
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
