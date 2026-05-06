package com.tom.tradereview.service;

import com.tom.tradereview.entity.MistakeTag;

import java.util.List;

/**
 * 错误标签字典服务。
 */
public interface MistakeTagService {
    List<MistakeTag> listOrderById();

    List<MistakeTag> list();

    MistakeTag getById(Long id);

    boolean save(MistakeTag mistakeTag);

    boolean updateById(MistakeTag mistakeTag);

    boolean removeById(Long id);
}
