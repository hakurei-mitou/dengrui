package com.mitou.dengrui.code_test;

import com.mitou.dengrui.pojo.VO.CardVO;
import com.mitou.dengrui.service.CardService;
import com.mitou.dengrui.service.CardSyncService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DataSyncTest {

    @Autowired
    private CardSyncService sqliteService;

    @Autowired
    private CardService cardService;


}
