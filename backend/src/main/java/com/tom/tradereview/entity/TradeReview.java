package com.tom.tradereview.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("trade_review")
public class TradeReview {
    private Long id;
    private Long tradeId;
    private String operationProcess;
    private String originalPlan;
    private String actualExecution;
    private String realProblem;
    private String improvementRule;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
