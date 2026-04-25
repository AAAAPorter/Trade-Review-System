package com.tom.tradereview.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tom.tradereview.entity.MistakeTag;
import com.tom.tradereview.service.MistakeTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mistake-tags")
public class MistakeTagController {
    private final MistakeTagService mistakeTagService;

    @GetMapping
    public List<MistakeTag> list() {
        return mistakeTagService.list(new LambdaQueryWrapper<MistakeTag>().orderByAsc(MistakeTag::getId));
    }
}
