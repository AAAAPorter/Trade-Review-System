package com.tom.tradereview.controller;

import com.tom.tradereview.dto.MistakeIdsDTO;
import com.tom.tradereview.entity.TradeMistakeRel;
import com.tom.tradereview.service.TradeMistakeRelService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 交易与错误标签的关系接口。
 *
 * <p>接口挂在 /api/trades 下，是因为前端通常围绕某一笔交易读取或替换其标签集合。</p>
 */
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trades")
public class TradeMistakeController {
    private final TradeMistakeRelService tradeMistakeRelService;

    @GetMapping("/{id}/mistakes")
    public List<Long> listMistakeIds(@PathVariable Long id) {
        return tradeMistakeRelService.listByTradeId(id)
                .stream()
                .map(TradeMistakeRel::getMistakeTagId)
                .toList();
    }

    /**
     * 保存时采用“先删后插”的替换模式，保证前端多选框传来的结果就是最终关系集合。
     */
    @PostMapping("/{id}/mistakes")
    @Transactional
    public Boolean replaceMistakes(@PathVariable Long id, @RequestBody MistakeIdsDTO dto) {
        tradeMistakeRelService.removeByTradeId(id);
        List<Long> mistakeTagIds = dto == null ? null : dto.getMistakeTagIds();
        if (mistakeTagIds == null || mistakeTagIds.isEmpty()) {
            return true;
        }
        return tradeMistakeRelService.saveBatch(mistakeTagIds.stream().map(tagId -> {
            TradeMistakeRel rel = new TradeMistakeRel();
            rel.setTradeId(id);
            rel.setMistakeTagId(tagId);
            return rel;
        }).toList());
    }
}
