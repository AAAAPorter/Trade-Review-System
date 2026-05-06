package com.tom.tradereview.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tom.tradereview.entity.TradeMistakeRel;
import com.tom.tradereview.mapper.TradeMistakeRelMapper;
import com.tom.tradereview.service.TradeMistakeRelService;
import org.springframework.stereotype.Service;

/**
 * 交易-错误标签关系服务实现。
 */
@Service
public class TradeMistakeRelServiceImpl extends ServiceImpl<TradeMistakeRelMapper, TradeMistakeRel> implements TradeMistakeRelService {
}
