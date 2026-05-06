package com.tom.tradereview.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 单笔成交明细。
 *
 * <p>一条交易记录可以包含多次买入和多次卖出；系统汇总字段全部从这些明细聚合而来。</p>
 */
@Data
@TableName("trade_execution_detail")
public class TradeExecutionDetail {
    private Long id;

    /** 所属交易记录 id。 */
    private Long tradeId;

    /** 成交方向，只允许 BUY 或 SELL。 */
    private String actionType;

    /** 实际成交时间，前端使用 YYYY-MM-DDTHH:mm:ss 传输。 */
    private LocalDateTime executionTime;

    /** 单次成交价格。 */
    private BigDecimal price;

    /** 单次成交数量。 */
    private Integer quantity;

    /** 仓位说明，例如 1层、加1层、减半、清仓。 */
    private String positionNote;

    /** 当次成交理由。 */
    private String reason;

    /** 补充备注。 */
    private String remark;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
