package com.mitou.dengrui.service;

import com.mitou.dengrui.pojo.DTO.CardDTO;
import com.mitou.dengrui.pojo.VO.CardVO;

import java.util.List;

public interface CardService {

    CardVO getCard(Integer id);

    Integer newCardIdWithoutLatest(List<Integer> latestCardIds);

    Integer addCard(CardDTO cardDTO, Boolean needHistoryAndSync);

    List<Integer> getAllCardIds();

    List<Integer> searchCardByWord(String keyword);

    List<String> searchCardFieldByWord(String cardTypeName, String cardFieldName, String keyword);

    List<String> latestCardField(String cardTypeName, String cardFieldName, Integer number);

    void deleteCard(Integer cardId, Boolean needHistoryAndSync);

    void updateCard(CardDTO cardDTO, Boolean needHistoryAndSync);
}
