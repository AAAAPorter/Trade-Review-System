package com.tom.tradereview.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tom.tradereview.entity.StockCompany;
import com.tom.tradereview.mapper.StockCompanyMapper;
import com.tom.tradereview.service.StockCompanyService;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

@Service
public class StockCompanyServiceImpl extends ServiceImpl<StockCompanyMapper, StockCompany> implements StockCompanyService {
    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 50;

    @Override
    public List<StockCompany> search(String keyword, Integer limit) {
        String text = trimToNull(keyword);
        if (text == null) {
            return List.of();
        }

        String searchName = normalizeStockName(text);
        int queryLimit = clampLimit(limit);
        LambdaQueryWrapper<StockCompany> query = new LambdaQueryWrapper<StockCompany>()
                .and(wrapper -> wrapper
                        .like(!searchName.isBlank(), StockCompany::getSearchName, searchName)
                        .or()
                        .like(StockCompany::getCode, text)
                        .or()
                        .like(StockCompany::getName, text))
                .orderByAsc(StockCompany::getCode)
                .last("LIMIT " + queryLimit);
        return list(query);
    }

    private int clampLimit(Integer limit) {
        if (limit == null) {
            return DEFAULT_LIMIT;
        }
        return Math.max(1, Math.min(limit, MAX_LIMIT));
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String normalizeStockName(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFKC)
                .replaceAll("\\s+", "")
                .toUpperCase(Locale.ROOT);
    }
}
