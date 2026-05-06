package com.tom.tradereview.service.impl;

import com.tom.tradereview.entity.TradeMistakeRel;
import com.tom.tradereview.mapper.TradeMistakeRelMapper;
import com.tom.tradereview.service.TradeMistakeRelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 交易-错误标签关系服务实现。
 */
@Service
@RequiredArgsConstructor
public class TradeMistakeRelServiceImpl implements TradeMistakeRelService {
    private final TradeMistakeRelMapper tradeMistakeRelMapper;

    @Override
    public List<TradeMistakeRel> listByTradeId(Long tradeId) {
        return tradeMistakeRelMapper.selectByTradeId(tradeId);
    }

    @Override
    public List<TradeMistakeRel> listByTradeIds(List<Long> tradeIds) {
        if (tradeIds == null || tradeIds.isEmpty()) {
            return List.of();
        }
        return tradeMistakeRelMapper.selectByTradeIds(tradeIds);
    }

    @Override
    public boolean removeByTradeId(Long tradeId) {
        tradeMistakeRelMapper.deleteByTradeId(tradeId);
        return true;
    }

    @Override
    public boolean removeByMistakeTagId(Long mistakeTagId) {
        tradeMistakeRelMapper.deleteByMistakeTagId(mistakeTagId);
        return true;
    }

    @Override
    public boolean saveBatch(List<TradeMistakeRel> relations) {
        if (relations == null || relations.isEmpty()) {
            return true;
        }
        return tradeMistakeRelMapper.insertBatch(relations) > 0;
    }
}
