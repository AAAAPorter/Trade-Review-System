package com.tom.tradereview.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tom.tradereview.entity.MistakeTag;
import com.tom.tradereview.mapper.MistakeTagMapper;
import com.tom.tradereview.service.MistakeTagService;
import org.springframework.stereotype.Service;

/**
 * 错误标签服务实现。
 *
 * <p>当前没有额外业务规则，继承 MyBatis Plus ServiceImpl 获得通用 CRUD 能力。</p>
 */
@Service
public class MistakeTagServiceImpl extends ServiceImpl<MistakeTagMapper, MistakeTag> implements MistakeTagService {
}
