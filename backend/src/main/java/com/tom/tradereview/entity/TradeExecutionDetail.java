package com.tom.tradereview.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("trade_execution_detail")
public class TradeExecutionDetail {
    private Long id;
    private Long tradeId;
    private String actionType;
    private LocalDateTime executionTime;
    private BigDecimal price;
    private Integer quantity;
    private String positionNote;
    private String reason;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
