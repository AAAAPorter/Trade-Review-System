package com.tom.tradereview.dto;

import com.tom.tradereview.entity.TradeExecutionDetail;
import com.tom.tradereview.entity.TradeRecord;
import lombok.Data;

import java.util.List;

/**
 * 新增交易时的组合入参。
 *
 * <p>用于把交易基础信息、草稿成交明细、错误标签一次性提交，后端在同一事务中落库。</p>
 */
@Data
public class TradeWithExecutionDetailsDTO {
    /** trade_record 主表字段。 */
    private TradeRecord tradeRecord;

    /** 新建交易时随表单一起提交的成交明细草稿。 */
    private List<TradeExecutionDetail> executionDetails;

    /** 交易关联的错误标签 id 列表。 */
    private List<Long> mistakeTagIds;
}
