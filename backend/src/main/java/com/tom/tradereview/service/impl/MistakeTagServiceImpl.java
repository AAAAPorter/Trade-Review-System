package com.tom.tradereview.service.impl;

import com.tom.tradereview.entity.MistakeTag;
import com.tom.tradereview.mapper.MistakeTagMapper;
import com.tom.tradereview.service.MistakeTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 错误标签服务实现。
 *
 * <p>当前没有额外业务规则，直接调用原生 MyBatis Mapper。</p>
 */
@Service
@RequiredArgsConstructor
public class MistakeTagServiceImpl implements MistakeTagService {
    private final MistakeTagMapper mistakeTagMapper;

    @Override
    public List<MistakeTag> listOrderById() {
        return mistakeTagMapper.selectAllOrderById();
    }

    @Override
    public List<MistakeTag> list() {
        return listOrderById();
    }

    @Override
    public MistakeTag getById(Long id) {
        return mistakeTagMapper.selectById(id);
    }

    @Override
    public boolean save(MistakeTag mistakeTag) {
        return mistakeTagMapper.insert(mistakeTag) > 0;
    }

    @Override
    public boolean updateById(MistakeTag mistakeTag) {
        return mistakeTagMapper.updateById(mistakeTag) > 0;
    }

    @Override
    public boolean removeById(Long id) {
        return mistakeTagMapper.deleteById(id) > 0;
    }
}
