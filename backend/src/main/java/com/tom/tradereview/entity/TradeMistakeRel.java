package com.tom.tradereview.entity;

import lombok.Data;

/**
 * 交易与错误标签的多对多关系表。
 */
@Data
public class TradeMistakeRel {
    private Long id;

    /** 交易记录 id。 */
    private Long tradeId;

    /** 错误标签 id。 */
    private Long mistakeTagId;
}
