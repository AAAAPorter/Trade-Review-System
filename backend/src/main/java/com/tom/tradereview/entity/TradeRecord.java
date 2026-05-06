package com.tom.tradereview.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 交易主表实体。
 *
 * <p>用户直接编辑的字段主要是股票、模式内外、观点、关键位；买卖时间、均价、盈亏、
 * 持仓状态等字段由成交明细服务根据 trade_execution_detail 自动反算。</p>
 */
@Data
@TableName("trade_record")
public class TradeRecord {
    private Long id;

    /** 股票代码，例如 000001。 */
    private String stockCode;

    /** 股票名称，用于列表展示和搜索过滤。 */
    private String stockName;

    /** 首次买入时间，来自所有买入明细中的最早成交时间。 */
    private LocalDateTime buyTime;

    /** 兼容旧字段：当前写入平均买入价。 */
    private BigDecimal buyPrice;

    /** 最后卖出时间，来自所有卖出明细中的最晚成交时间。 */
    private LocalDateTime sellTime;

    /** 兼容旧字段：当前写入平均卖出价。 */
    private BigDecimal sellPrice;

    private Integer positionLevel;
    private BigDecimal stopLossPrice;
    private String buyReason;
    private String sellReason;

    /** 交易观察：外部观点、老师观点或复盘参考。 */
    private String teacherOpinion;

    /** 关键价位、失效位等交易前提。 */
    private String keyLevel;

    /** 已实现盈亏；持仓中未平仓时为空。 */
    private BigDecimal profitAmount;

    /** 已实现盈亏率；完整清仓或部分平仓时按已卖出部分成本计算。 */
    private BigDecimal profitRate;

    /** 1 表示模式内交易，0 表示模式外交易。 */
    private Integer isPatternTrade;

    /** 统计归属日期：优先使用最后卖出日期，否则使用首次买入日期。 */
    private LocalDate tradeDate;

    /** 累计买入数量，等于所有 BUY 明细 quantity 之和。 */
    private Integer totalBuyQuantity;

    /** 累计卖出数量，等于所有 SELL 明细 quantity 之和。 */
    private Integer totalSellQuantity;

    /** 当前剩余数量 = 累计买入数量 - 累计卖出数量。 */
    private Integer remainingQuantity;

    /** 成交金额加权后的平均买入价。 */
    private BigDecimal avgBuyPrice;

    /** 成交金额加权后的平均卖出价。 */
    private BigDecimal avgSellPrice;

    /** OPEN 持仓中、PARTIAL_CLOSED 部分平仓、CLOSED 已清仓。 */
    private String positionStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
