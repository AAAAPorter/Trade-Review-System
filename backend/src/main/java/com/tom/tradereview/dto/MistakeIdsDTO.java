package com.tom.tradereview.dto;

import lombok.Data;

import java.util.List;

/**
 * 前端保存交易错误标签多选结果时的请求体。
 */
@Data
public class MistakeIdsDTO {
    /** 被选中的错误标签 id 集合。 */
    private List<Long> mistakeTagIds;
}
