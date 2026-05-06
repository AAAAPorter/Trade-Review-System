package com.tom.tradereview.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 周复盘实体。
 *
 * <p>前半部分保存统计快照，后半部分保存人工复盘内容和下周纪律。
 * 这样历史周复盘不会因为之后补录交易而自动变化。</p>
 */
@Data
public class WeeklyReview {
    private Long id;

    /** 周期开始日期。 */
    private LocalDate weekStart;

    /** 周期结束日期。 */
    private LocalDate weekEnd;

    private BigDecimal startCapital;
    private BigDecimal endCapital;

    /** 本周期盈亏金额快照。 */
    private BigDecimal profitAmount;

    /** 本周期收益率快照。 */
    private BigDecimal profitRate;

    /** 本周期交易笔数。 */
    private Long tradeCount;

    private Long winCount;
    private Long lossCount;
    private BigDecimal winRate;
    private Long patternTradeCount;
    private Long nonPatternTradeCount;

    /** 高频错误标签摘要，例如：追高(3), 不止损(2)。 */
    private String topMistakeSummary;

    private String biggestWinTrade;
    private String biggestLossTrade;

    /** 本周赚钱来自什么动作或行情。 */
    private String profitSource;

    /** 本周亏钱来自什么动作或问题。 */
    private String lossSource;

    /** 本周最需要解决的问题。 */
    private String biggestProblem;

    /** 本周执行得最好的动作。 */
    private String bestAction;

    /** 下周盘中纪律一。 */
    private String ruleOne;

    /** 下周盘中纪律二。 */
    private String ruleTwo;

    /** 下周盘中纪律三。 */
    private String ruleThree;

    /** 下周刻意训练主题。 */
    private String trainingTopic;

    /** 训练方法或执行安排。 */
    private String trainingMethod;

    /** 主观执行评分，前端限制为 0 到 10。 */
    private Integer executionScore;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
