package com.tom.tradereview.entity;

import lombok.Data;

/**
 * 错误标签字典。
 *
 * <p>标签用于给交易归因，后续周统计会统计这些标签出现频率。</p>
 */
@Data
public class MistakeTag {
    private Long id;

    /** 标签名称，例如“追高”“未按计划止损”。 */
    private String name;

    /** 标签说明，帮助后续回看时统一判定标准。 */
    private String description;
}
