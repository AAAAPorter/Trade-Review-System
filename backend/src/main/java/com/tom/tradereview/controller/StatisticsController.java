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

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping("/week")
    public WeeklyStatisticsDTO week(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return statisticsService.week(start, end);
    }
}
