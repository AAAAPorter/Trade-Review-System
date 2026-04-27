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
