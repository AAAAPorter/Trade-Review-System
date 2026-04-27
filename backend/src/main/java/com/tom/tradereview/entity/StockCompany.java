package com.tom.tradereview.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("stock_company")
public class StockCompany {
    @TableId("code")
    private String code;
    private String name;
    private String searchName;
    private String market;
    private String exchange;
    private String source;
    private LocalDateTime fetchedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
