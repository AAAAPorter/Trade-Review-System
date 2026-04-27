package com.tom.tradereview.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trades")
public class TradeMistakeController {
    private final TradeMistakeRelService tradeMistakeRelService;

    @GetMapping("/{id}/mistakes")
    public List<Long> listMistakeIds(@PathVariable Long id) {
        return tradeMistakeRelService.list(new LambdaQueryWrapper<TradeMistakeRel>().eq(TradeMistakeRel::getTradeId, id))
                .stream()
                .map(TradeMistakeRel::getMistakeTagId)
                .toList();
    }

    @PostMapping("/{id}/mistakes")
    @Transactional
    public Boolean replaceMistakes(@PathVariable Long id, @RequestBody MistakeIdsDTO dto) {
        tradeMistakeRelService.remove(new LambdaQueryWrapper<TradeMistakeRel>().eq(TradeMistakeRel::getTradeId, id));
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
