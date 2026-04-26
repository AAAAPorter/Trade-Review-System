package com.tom.tradereview.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("weekly_review")
public class WeeklyReview {
    private Long id;
    private LocalDate weekStart;
    private LocalDate weekEnd;
    private BigDecimal startCapital;
    private BigDecimal endCapital;
    private BigDecimal profitAmount;
    private BigDecimal profitRate;
    private Long tradeCount;
    private Long winCount;
    private Long lossCount;
    private BigDecimal winRate;
    private Long patternTradeCount;
    private Long nonPatternTradeCount;
    private String topMistakeSummary;
    private String biggestWinTrade;
    private String biggestLossTrade;
    private String profitSource;
    private String lossSource;
    private String biggestProblem;
    private String bestAction;
    private String ruleOne;
    private String ruleTwo;
    private String ruleThree;
    private String trainingTopic;
    private String trainingMethod;
    private Integer executionScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
