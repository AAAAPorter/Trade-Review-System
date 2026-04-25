package com.tom.tradereview.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tom.tradereview.entity.TradeRecord;
import com.tom.tradereview.mapper.TradeRecordMapper;
import com.tom.tradereview.service.TradeRecordService;
import org.springframework.stereotype.Service;

@Service
public class TradeRecordServiceImpl extends ServiceImpl<TradeRecordMapper, TradeRecord> implements TradeRecordService {
}
