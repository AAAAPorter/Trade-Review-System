package com.tom.tradereview.dto;

import com.tom.tradereview.entity.TradeExecutionDetail;
import com.tom.tradereview.entity.TradeRecord;
import lombok.Data;

import java.util.List;

@Data
public class TradeWithExecutionDetailsDTO {
    private TradeRecord tradeRecord;
    private List<TradeExecutionDetail> executionDetails;
    private List<Long> mistakeTagIds;
}
