package com.tom.tradereview.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("mistake_tag")
public class MistakeTag {
    private Long id;
    private String name;
    private String description;
}
