package com.tom.tradereview.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tom.tradereview.entity.StockCompany;

import java.util.List;

/**
 * 股票公司搜索服务。
 */
public interface StockCompanyService extends IService<StockCompany> {
    /**
     * 按股票名称、搜索名或代码模糊搜索，limit 控制前端下拉候选数量。
     */
    List<StockCompany> search(String keyword, Integer limit);
}
