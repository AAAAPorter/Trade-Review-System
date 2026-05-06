package com.tom.tradereview.controller;

import com.tom.tradereview.entity.StockCompany;
import com.tom.tradereview.service.StockCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * A 股公司搜索接口。
 *
 * <p>用于交易表单的股票名称自动补全，并在匹配后回填股票代码。</p>
 */
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stocks")
public class StockCompanyController {
    private final StockCompanyService stockCompanyService;

    @GetMapping
    public List<StockCompany> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer limit
    ) {
        return stockCompanyService.search(keyword, limit);
    }
}
