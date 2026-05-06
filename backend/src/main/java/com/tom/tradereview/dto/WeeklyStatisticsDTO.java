package com.tom.tradereview.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 周统计返回对象。
 *
 * <p>首页仪表盘和周复盘页共用这个结构；周复盘保存时会把其中关键字段落到 weekly_review。</p>
 */
@Data
public class WeeklyStatisticsDTO {
    /** 统计周期内交易笔数。 */
    private Long tradeCount;

    private Long winCount;
    private Long lossCount;
    private BigDecimal winRate;

    /** 周期内已实现盈亏金额合计。 */
    private BigDecimal profitAmount;

    /** 当前预留字段，后续可接入资金曲线后计算。 */
    private BigDecimal profitRate;

    private Long patternTradeCount;
    private Long nonPatternTradeCount;

    /** 高频错误标签排行，最多返回 5 条。 */
    private List<MistakeCountDTO> topMistakes;

    /** 便于周复盘表单直接填充的错误摘要文本。 */
    private String topMistakeSummary;

    private String biggestWinTrade;
    private String biggestLossTrade;
}
