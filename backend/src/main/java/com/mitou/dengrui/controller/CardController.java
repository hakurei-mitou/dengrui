package com.mitou.dengrui.controller;

import com.mitou.dengrui.pojo.DTO.CardDTO;
import com.mitou.dengrui.pojo.VO.CardVO;
import com.mitou.dengrui.pojo.entity.Card;
import com.mitou.dengrui.service.CardService;
import com.mitou.dengrui.service.HistoryService;
import com.mitou.dengrui.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class CardController {

    @Autowired
    private CardService cardService;
    @Autowired
    private HistoryService historyService;

    @GetMapping("/card")
    public Result getCard(Integer id) {
        log.info("进入 getCard : id = {}", id);

        CardVO cardVO = cardService.getCard(id);

        return Result.success(cardVO);
    }

    @GetMapping("/newCardId")
    public Result newCardId() {
        log.info("进入 newCardId");
        // 默认最近 10 个 card 不重复，边界情况时，选择最远的不相同的 card ，否则返回 null 。
        List<Integer> latestCardIds = historyService.getLatestHisotryCardIds(10);

        Integer cardId = cardService.newCardIdWithoutLatest(latestCardIds);

        return Result.success(cardId);
    }

    @PostMapping("/addCard")
    public Result addCard(@RequestBody CardDTO cardDTO) {
        log.info("进入 addCard : card = {}", cardDTO);

        cardService.addCard(cardDTO, true);

        return  Result.success();
    }

    @GetMapping("/allCardIds")
    public Result getAllCardIds() {
        log.info("进入 getAllCardIds");

        List<Integer> cardIds = cardService.getAllCardIds();

        return  Result.success(cardIds);
    }

    @GetMapping("/searchCard")
    public Result seachCard(String keyword) {
        log.info("进入 searchCard");

        List<Integer> cardIds = cardService.searchCardByWord(keyword);

        return Result.success(cardIds);
    }

    @GetMapping("/searchCardField")
    public Result seachCardField(String cardTypeName, String cardFieldName, String keyword) {
        log.info("进入 searchCardField");

        cardFieldName = toUnderCase(cardFieldName);

        String existingTypeName = "literature song words";
        if (!existingTypeName.contains(cardTypeName)) {
            return Result.error("cardTypeName error!");
        }

        List<String> fields = cardService.searchCardFieldByWord(cardTypeName, cardFieldName, keyword);

        return Result.success(fields);
    }

    private static String toUnderCase(String name) {
        if (name == null) {
            return null;
        }

        int len = name.length();
        StringBuilder res = new StringBuilder(len + 2);
        char pre = 0;
        for (int i = 0; i < len; i++) {
            char ch = name.charAt(i);
            if (Character.isUpperCase(ch)) {
                if (pre != '_') {
                    res.append('_');
                }
                res.append(Character.toLowerCase(ch));
            } else {
                res.append(ch);
            }
            pre = ch;
        }
        return res.toString();
    }

    @GetMapping("/latestCardField")
    public Result latestCardField(String cardTypeName, String cardFieldName, Integer number) {
        log.info("进入 latestCardField parameter = " + cardTypeName +", "+ cardFieldName);

        cardFieldName = toUnderCase(cardFieldName);

        String existingTypeName = "literature song words";
        if (!existingTypeName.contains(cardTypeName)) {
            return Result.error("cardTypeName error!");
        }

        List<String> fields = cardService.latestCardField(cardTypeName, cardFieldName, number);

        return Result.success(fields);
    }

    @PostMapping("/deleteCard")
    public Result deleteCard(Integer cardId) {
        log.info("进入 deleteCard");

        cardService.deleteCard(cardId, true);

        return  Result.success();
    }

    @PostMapping("updateCard")
    public Result updateCard(@RequestBody CardDTO cardDTO) {
        log.info("### enter updateCard ###");
        System.out.println("cardData = "+ cardDTO);

        cardService.updateCard(cardDTO, true);

        return Result.success();
    }

}
