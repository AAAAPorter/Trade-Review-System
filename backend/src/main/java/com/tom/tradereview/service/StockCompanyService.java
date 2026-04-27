package com.tom.tradereview.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tom.tradereview.entity.StockCompany;

import java.util.List;

public interface StockCompanyService extends IService<StockCompany> {
    List<StockCompany> search(String keyword, Integer limit);
}
