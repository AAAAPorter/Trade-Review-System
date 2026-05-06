package com.tom.tradereview.controller;

import com.tom.tradereview.dto.WeeklyStatisticsDTO;
import com.tom.tradereview.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 统计接口。
 *
 * <p>当前只提供周统计，供首页仪表盘和周复盘页复用。</p>
 */
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;

    /**
     * 按闭区间 [start, end] 统计交易表现和高频错误。
     */
    @GetMapping("/week")
    public WeeklyStatisticsDTO week(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return statisticsService.week(start, end);
    }
}
