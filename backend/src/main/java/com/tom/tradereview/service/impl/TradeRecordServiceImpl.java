package com.tom.tradereview.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tom.tradereview.entity.TradeExecutionDetail;
import com.tom.tradereview.entity.TradeMistakeRel;
import com.tom.tradereview.entity.TradeRecord;
import com.tom.tradereview.entity.TradeReview;
import com.tom.tradereview.mapper.TradeExecutionDetailMapper;
import com.tom.tradereview.mapper.TradeMistakeRelMapper;
import com.tom.tradereview.mapper.TradeRecordMapper;
import com.tom.tradereview.mapper.TradeReviewMapper;
import com.tom.tradereview.service.TradeRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TradeRecordServiceImpl extends ServiceImpl<TradeRecordMapper, TradeRecord> implements TradeRecordService {
    private final TradeMistakeRelMapper tradeMistakeRelMapper;
    private final TradeExecutionDetailMapper tradeExecutionDetailMapper;
    private final TradeReviewMapper tradeReviewMapper;

    @Override
    public TradeRecord createTrade(TradeRecord tradeRecord) {
        TradeRecord payload = editablePayload(tradeRecord);
        validateBaseInfo(payload);
        save(payload);
        return payload;
    }

    @Override
    public TradeRecord updateTrade(Long id, TradeRecord tradeRecord) {
        if (id == null || getById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "交易记录不存在");
        }
        TradeRecord payload = editablePayload(tradeRecord);
        payload.setId(id);
        validateBaseInfo(payload);
        updateById(payload);
        return getById(id);
    }

    @Override
    @Transactional
    public boolean deleteTrade(Long id) {
        if (id == null || getById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "交易记录不存在");
        }
        tradeExecutionDetailMapper.delete(new LambdaQueryWrapper<TradeExecutionDetail>().eq(TradeExecutionDetail::getTradeId, id));
        tradeMistakeRelMapper.delete(new LambdaQueryWrapper<TradeMistakeRel>().eq(TradeMistakeRel::getTradeId, id));
        tradeReviewMapper.delete(new LambdaQueryWrapper<TradeReview>().eq(TradeReview::getTradeId, id));
        return removeById(id);
    }

    private TradeRecord editablePayload(TradeRecord source) {
        if (source == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "交易基础信息不能为空");
        }
        TradeRecord payload = new TradeRecord();
        payload.setStockCode(trimToNull(source.getStockCode()));
        payload.setStockName(trimToNull(source.getStockName()));
        payload.setIsPatternTrade(source.getIsPatternTrade() == null ? 1 : source.getIsPatternTrade());
        payload.setTeacherOpinion(source.getTeacherOpinion());
        payload.setKeyLevel(source.getKeyLevel());
        return payload;
    }

    private void validateBaseInfo(TradeRecord tradeRecord) {
        if (tradeRecord.getStockCode() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "股票代码不能为空");
        }
        if (tradeRecord.getStockName() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "股票名称不能为空");
        }
        if (!Integer.valueOf(0).equals(tradeRecord.getIsPatternTrade()) && !Integer.valueOf(1).equals(tradeRecord.getIsPatternTrade())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "isPatternTrade 只允许 0 或 1");
        }
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
