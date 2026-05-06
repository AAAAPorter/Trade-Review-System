package com.tom.tradereview.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * A 股公司基础信息。
 *
 * <p>前端交易表单通过 name/searchName 做自动补全，并在选中后回填 code。</p>
 */
@Data
@TableName("stock_company")
public class StockCompany {
    /** 股票代码作为主键。 */
    @TableId("code")
    private String code;

    /** 证券简称。 */
    private String name;

    /** 搜索辅助字段，可存拼音、别名或归一化名称。 */
    private String searchName;

    private String market;
    private String exchange;
    private String source;

    /** 外部数据源抓取时间。 */
    private LocalDateTime fetchedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
