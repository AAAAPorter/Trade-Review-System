package com.tom.tradereview.dto;

import lombok.Data;

/**
 * 错误标签出现次数，用于错误排行图表和周复盘摘要。
 */
@Data
public class MistakeCountDTO {
    /** 错误标签名称。 */
    private String name;

    /** 出现次数。 */
    private Long count;
}
