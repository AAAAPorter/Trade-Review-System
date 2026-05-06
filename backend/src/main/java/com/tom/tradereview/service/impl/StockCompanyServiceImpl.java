package com.tom.tradereview.service.impl;

import com.tom.tradereview.entity.StockCompany;
import com.tom.tradereview.mapper.StockCompanyMapper;
import com.tom.tradereview.service.StockCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

/**
 * 股票公司搜索实现。
 *
 * <p>为了让前端输入“全角字符、带空格、大小写不一致”的名称时仍能命中，
 * 查询前会先做 NFKC 归一化、去空格和转大写。</p>
 */
@Service
@RequiredArgsConstructor
public class StockCompanyServiceImpl implements StockCompanyService {
    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 50;

    private final StockCompanyMapper stockCompanyMapper;

    /**
     * 按归一化名称、代码、原始名称三路模糊匹配，并限制返回数量，避免自动补全列表过长。
     */
    @Override
    public List<StockCompany> search(String keyword, Integer limit) {
        String text = trimToNull(keyword);
        if (text == null) {
            return List.of();
        }

        String searchName = normalizeStockName(text);
        int queryLimit = clampLimit(limit);
        return stockCompanyMapper.search(searchName, text, queryLimit);
    }

    /**
     * 限制前端传入的候选数量，防止一次请求返回过多数据。
     */
    private int clampLimit(Integer limit) {
        if (limit == null) {
            return DEFAULT_LIMIT;
        }
        return Math.max(1, Math.min(limit, MAX_LIMIT));
    }

    /**
     * 空白输入直接视为 null，避免无关键词时全表扫描。
     */
    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    /**
     * 股票名称搜索归一化：全角转半角、去空白、转大写。
     */
    private String normalizeStockName(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFKC)
                .replaceAll("\\s+", "")
                .toUpperCase(Locale.ROOT);
    }
}
