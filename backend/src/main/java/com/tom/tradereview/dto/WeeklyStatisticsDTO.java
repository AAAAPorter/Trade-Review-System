package com.tom.tradereview.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class WeeklyStatisticsDTO {
    private Long tradeCount;
    private Long winCount;
    private Long lossCount;
    private BigDecimal winRate;
    private BigDecimal profitAmount;
    private BigDecimal profitRate;
    private Long patternTradeCount;
    private Long nonPatternTradeCount;
    private List<MistakeCountDTO> topMistakes;
    private String topMistakeSummary;
    private String biggestWinTrade;
    private String biggestLossTrade;
}
