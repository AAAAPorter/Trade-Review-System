package com.tom.tradereview.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("trade_record")
public class TradeRecord {
    private Long id;
    private String stockCode;
    private String stockName;
    private LocalDateTime buyTime;
    private BigDecimal buyPrice;
    private LocalDateTime sellTime;
    private BigDecimal sellPrice;
    private Integer positionLevel;
    private BigDecimal stopLossPrice;
    private String buyReason;
    private String sellReason;
    private String teacherOpinion;
    private String keyLevel;
    private BigDecimal profitAmount;
    private BigDecimal profitRate;
    private Integer isPatternTrade;
    private LocalDate tradeDate;
    private Integer totalBuyQuantity;
    private Integer totalSellQuantity;
    private Integer remainingQuantity;
    private BigDecimal avgBuyPrice;
    private BigDecimal avgSellPrice;
    private String positionStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
