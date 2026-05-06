package com.tom.tradereview.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 单笔交易复盘。
 *
 * <p>记录从“当时怎么想”到“真正问题”和“下一次规则”的完整反思链路。</p>
 */
@Data
@TableName("trade_review")
public class TradeReview {
    private Long id;

    /** 对应的交易记录 id。 */
    private Long tradeId;

    /** 实际操作经过。 */
    private String operationProcess;

    /** 交易前或当时的原计划。 */
    private String originalPlan;

    /** 实际执行与原计划的偏差。 */
    private String actualExecution;

    /** 复盘后定位到的真正问题。 */
    private String realProblem;

    /** 下次可执行的改进规则，前端限制为短句。 */
    private String improvementRule;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
