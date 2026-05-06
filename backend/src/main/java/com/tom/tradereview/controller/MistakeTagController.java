package com.tom.tradereview.controller;

import com.tom.tradereview.entity.MistakeTag;
import com.tom.tradereview.service.MistakeTagService;
import com.tom.tradereview.service.TradeMistakeRelService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 错误标签维护接口。
 *
 * <p>标签本身是字典数据；交易与标签的绑定关系放在 trade_mistake_rel 表中。</p>
 */
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mistake-tags")
public class MistakeTagController {
    private final MistakeTagService mistakeTagService;
    private final TradeMistakeRelService tradeMistakeRelService;

    @GetMapping
    public List<MistakeTag> list() {
        return mistakeTagService.listOrderById();
    }

    @GetMapping("/{id}")
    public MistakeTag detail(@PathVariable Long id) {
        return mistakeTagService.getById(id);
    }

    @PostMapping
    public MistakeTag create(@RequestBody MistakeTag mistakeTag) {
        mistakeTagService.save(mistakeTag);
        return mistakeTag;
    }

    @PutMapping("/{id}")
    public MistakeTag update(@PathVariable Long id, @RequestBody MistakeTag mistakeTag) {
        mistakeTag.setId(id);
        mistakeTagService.updateById(mistakeTag);
        return mistakeTagService.getById(id);
    }

    /**
     * 删除标签前先清理交易-标签关系，避免留下孤儿关系影响统计。
     */
    @DeleteMapping("/{id}")
    @Transactional
    public Boolean delete(@PathVariable Long id) {
        tradeMistakeRelService.removeByMistakeTagId(id);
        return mistakeTagService.removeById(id);
    }
}
