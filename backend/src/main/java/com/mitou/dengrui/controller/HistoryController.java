package com.mitou.dengrui.controller;

import com.mitou.dengrui.pojo.VO.CardVO;
import com.mitou.dengrui.service.HistoryService;
import com.mitou.dengrui.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    @GetMapping("/history")
    public Result getLatestHistoryCardIds(Integer latestNumber) {
        log.info("进入 getLatestHistoryCardIds : latestNumber = {}", latestNumber);

        List<Integer> cardIds = historyService.getLatestHisotryCardIds(latestNumber);
        return Result.success(cardIds);
    }

    @PostMapping("/history")
    public Result push2History(Integer cardId, Integer type) {
        log.info("进入 push2History : cardId = {}, type = {}", cardId, type);
        historyService.push2History(cardId, type);
        return Result.success();
    }

    @GetMapping("/latestReadInAllCardIds")
    public Result getLatestReadInAllCardIds(Integer latestNumber) {
        log.info("进入 getLatestReadInAllCardIds : latestNumber = {}", latestNumber);

        List<Integer> cardIds = historyService.getLatestReadInAllCardIds(latestNumber);
        return Result.success(cardIds);
    }

}
