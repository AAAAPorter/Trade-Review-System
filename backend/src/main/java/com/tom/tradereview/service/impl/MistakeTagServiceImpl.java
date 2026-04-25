package com.tom.tradereview.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tom.tradereview.entity.MistakeTag;
import com.tom.tradereview.mapper.MistakeTagMapper;
import com.tom.tradereview.service.MistakeTagService;
import org.springframework.stereotype.Service;

@Service
public class MistakeTagServiceImpl extends ServiceImpl<MistakeTagMapper, MistakeTag> implements MistakeTagService {
}
