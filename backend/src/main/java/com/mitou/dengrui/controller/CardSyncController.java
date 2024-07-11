package com.mitou.dengrui.controller;

import com.mitou.dengrui.mapper.SyncMapper;
import com.mitou.dengrui.pojo.DTO.GitHubInfo;
import com.mitou.dengrui.service.CardService;
import com.mitou.dengrui.service.HistoryService;
import com.mitou.dengrui.service.CardSyncService;
import com.mitou.dengrui.utils.GitHubFileManager;
import com.mitou.dengrui.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class CardSyncController {

    @Autowired
    private CardSyncService cardSyncService;

    @Autowired
    private CardService cardService;

    @Autowired
    private GitHubFileManager gitHubFileManager;

    @Autowired
    private SyncMapper syncMapper;

    @GetMapping("/tryConnectGitHub")
    public Result tryConnectGitHub() {

        if (gitHubFileManager.tryConnectGitHub()) {
            return Result.success();
        } else {
            return Result.error("Connection Failed! \n Please check network or set the GitHub info!");
        }
    }

    @PostMapping("/setGitHubInfo")
    public Result setGitHubInfo(@RequestBody GitHubInfo gitHubInfo) {
        GitHubFileManager testManager = new GitHubFileManager(gitHubInfo);
        if (testManager.tryConnectGitHub()) {
            syncMapper.setInfo(gitHubInfo);
            gitHubFileManager.init();
            return Result.success();
        } else {
            return Result.error("The info is not valid!");
        }
    }

    @PostMapping("/sync")
    public void syncSQLiteAndGitHub() {
        cardSyncService.syncSQLiteAndGitHub(cardService);
    }

}
