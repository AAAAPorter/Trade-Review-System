package com.tom.tradereview.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tom.tradereview.dto.MistakeCountDTO;
import com.tom.tradereview.dto.WeeklyStatisticsDTO;
import com.tom.tradereview.entity.MistakeTag;
import com.tom.tradereview.entity.TradeMistakeRel;
import com.tom.tradereview.entity.TradeRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final TradeRecordService tradeRecordService;
    private final TradeMistakeRelService tradeMistakeRelService;
    private final MistakeTagService mistakeTagService;

    public WeeklyStatisticsDTO week(LocalDate start, LocalDate end) {
        List<TradeRecord> trades = tradeRecordService.list(new LambdaQueryWrapper<TradeRecord>()
                .ge(TradeRecord::getTradeDate, start)
                .le(TradeRecord::getTradeDate, end));

        long tradeCount = trades.size();
        long winCount = trades.stream().filter(trade -> positive(trade.getProfitAmount())).count();
        long lossCount = trades.stream().filter(trade -> negative(trade.getProfitAmount())).count();
        BigDecimal profitAmount = trades.stream()
                .map(TradeRecord::getProfitAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long patternTradeCount = trades.stream().filter(trade -> Integer.valueOf(1).equals(trade.getIsPatternTrade())).count();

        WeeklyStatisticsDTO dto = new WeeklyStatisticsDTO();
        dto.setTradeCount(tradeCount);
        dto.setWinCount(winCount);
        dto.setLossCount(lossCount);
        dto.setWinRate(tradeCount == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(winCount).divide(BigDecimal.valueOf(tradeCount), 4, RoundingMode.HALF_UP));
        dto.setProfitAmount(profitAmount);
        dto.setProfitRate(BigDecimal.ZERO);
        dto.setPatternTradeCount(patternTradeCount);
        dto.setNonPatternTradeCount(tradeCount - patternTradeCount);
        dto.setTopMistakes(topMistakes(trades));
        dto.setBiggestWinTrade(bestTradeName(trades, true));
        dto.setBiggestLossTrade(bestTradeName(trades, false));
        return dto;
    }

    private List<MistakeCountDTO> topMistakes(List<TradeRecord> trades) {
        List<Long> tradeIds = trades.stream().map(TradeRecord::getId).filter(Objects::nonNull).toList();
        if (tradeIds.isEmpty()) {
            return List.of();
        }
        Map<Long, String> tagNames = mistakeTagService.list().stream()
                .collect(Collectors.toMap(MistakeTag::getId, MistakeTag::getName));
        Map<Long, Long> counts = tradeMistakeRelService.list(new LambdaQueryWrapper<TradeMistakeRel>().in(TradeMistakeRel::getTradeId, tradeIds))
                .stream()
                .collect(Collectors.groupingBy(TradeMistakeRel::getMistakeTagId, Collectors.counting()));
        return counts.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(5)
                .map(entry -> {
                    MistakeCountDTO dto = new MistakeCountDTO();
                    dto.setName(tagNames.getOrDefault(entry.getKey(), "unknown"));
                    dto.setCount(entry.getValue());
                    return dto;
                })
                .toList();
    }

    private String bestTradeName(List<TradeRecord> trades, boolean biggestWin) {
        Comparator<TradeRecord> comparator = Comparator.comparing(
                trade -> trade.getProfitAmount() == null ? BigDecimal.ZERO : trade.getProfitAmount()
        );
        return (biggestWin ? trades.stream().max(comparator) : trades.stream().min(comparator))
                .map(TradeRecord::getStockName)
                .orElse(null);
    }

    private boolean positive(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean negative(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) < 0;
    }
}
