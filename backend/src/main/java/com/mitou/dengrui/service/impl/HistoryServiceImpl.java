package com.mitou.dengrui.service.impl;

import com.mitou.dengrui.constant.HistoryType;
import com.mitou.dengrui.enumeration.OperationType;
import com.mitou.dengrui.mapper.CardMapper;
import com.mitou.dengrui.mapper.HistoryMapper;
import com.mitou.dengrui.pojo.entity.History;
import com.mitou.dengrui.service.HistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class HistoryServiceImpl implements HistoryService {

    @Autowired
    HistoryMapper historyMapper;


    @Override
    public List<Integer> getLatestHisotryCardIds(Integer latestNumber) {
        // history table does not have deleted flag.
        List<Integer> historyCardIds = historyMapper.getAllCardIds();

        Integer total = historyMapper.getTotalNumber();

        Integer beginIndex = total - latestNumber;
        Integer offset = latestNumber;

        List<History> originalHistories = historyMapper.getHistoriesUsingLimit(beginIndex, offset);

        // process histories of deleted cards
        // If a card has been deleted, its cardId will not be regenerated because of autoincrement primary key.
        List<Integer> cardIds = new ArrayList<Integer>();
        List<Integer> deletedCardIds = new ArrayList<Integer>();
        for (int i = originalHistories.size() - 1; i >= 0; --i) {
            Integer cardId = originalHistories.get(i).getCardId();
            Integer type = originalHistories.get(i).getType();
            if (Objects.equals(type, HistoryType.DELETE)) {
                deletedCardIds.add(cardId);
            } else if (! deletedCardIds.contains(cardId)) {
                cardIds.add(cardId);
            }
        }

        return cardIds;
    }

    @Override
    public void push2History(Integer cardId, Integer type) {

        if (cardId < 1) {
            System.out.println("### cardId Error! ###");
        }

        History history = History.builder()
                .cardId(cardId)
                .type(type)
                .build();
        historyMapper.insert2Tail(history);
    }

    @Autowired
    CardMapper cardMapper;

    @Override
    public List<Integer> getLatestReadInAllCardIds(Integer latestNumber) {
        List<Integer> cardIds = historyMapper.getTypeCardIds(HistoryType.READINALL);
//        System.out.println("typeCardIds = " + cardIds);

        if (cardIds.isEmpty()) {
            // show latest cardIds
           cardIds = cardMapper.getAllCardIds();
        }

        int beginIndex = cardIds.size() - latestNumber;
        if (beginIndex < 0) {
            beginIndex = 0;
            System.out.println("### No enough number cardIds! ###");
        }
        List<Integer> latestCardIds = cardIds.subList(beginIndex, cardIds.size());

        return latestCardIds;
    }
}
