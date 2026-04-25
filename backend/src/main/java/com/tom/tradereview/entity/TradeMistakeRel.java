package com.tom.tradereview.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("trade_mistake_rel")
public class TradeMistakeRel {
    private Long id;
    private Long tradeId;
    private Long mistakeTagId;
}
