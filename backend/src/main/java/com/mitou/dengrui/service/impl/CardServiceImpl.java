package com.mitou.dengrui.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mitou.dengrui.constant.CardType;
import com.mitou.dengrui.constant.HistoryType;
import com.mitou.dengrui.mapper.*;
import com.mitou.dengrui.pojo.DTO.CardDTO;
import com.mitou.dengrui.pojo.entity.*;
import com.mitou.dengrui.pojo.VO.CardVO;
import com.mitou.dengrui.service.CardService;
import com.mitou.dengrui.service.CardSyncService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class CardServiceImpl implements CardService {

    @Autowired
    private CardMapper cardMapper;
    @Autowired
    private LiteratureMapper literatureMapper;
    @Autowired
    private SongMapper songMapper;
    @Autowired
    private WordsMapper wordsMapper;
    @Autowired
    private HistoryMapper historyMapper;

    @Autowired
    private CardSyncService cardSyncService;

    @Override
    public CardVO getCard(Integer id) {

        Card card = cardMapper.getCardById(id);

        log.info("getCardById return Card = {}", card);

        if (card == null) {
            System.out.println("card may have been deleted!");
            return null;
        }

        CardVO cardVO = CardVO.builder().build();
        BeanUtils.copyProperties(card, cardVO);

        int type = card.getType();
        int contentId = card.getContentId();
        if (type == CardType.LITERATURE) {
            Literature literature = literatureMapper.getLiteratureById(contentId);
            cardVO.setContent(literature);

        } else if (type == CardType.SONG) {
            Song song = songMapper.getSongById(contentId);
            cardVO.setContent(song);

        } else if (type == CardType.WORDS) {
            Words words = wordsMapper.getWordsById(contentId);
            cardVO.setContent(words);

        } else {
            assert false : "unknown card type error";
        }

        log.info("返回 cardVO = {}", cardVO);

        return cardVO;
    }

    @Override
    public Integer newCardIdWithoutLatest(List<Integer> latestCardIds) {
        Integer totalCardNumber = cardMapper.getTotalNumber();

        if (totalCardNumber == 0) {
            return null;
        }

        Random randomGenerator = new Random();
        Integer cardId = null;
        Integer genCount = 0;
        while (true) {
            // return [1, total + 1)，rank begin on 1
            Integer rank = randomGenerator.nextInt(1, totalCardNumber + 1);   // 是第几个 card，而不是具体的 cardId
            cardId = cardMapper.getCardIdByRank(rank);
            genCount++;
            log.info("###### latestCardIds = {}, cardId = {}, genCount = {}######", latestCardIds, cardId, genCount);

            if (latestCardIds.contains(cardId)) {
                if (genCount > 10) {
                    Integer presentCardId = latestCardIds.get(latestCardIds.size() - 1);
                    for (int i = 0; i < latestCardIds.size(); i++) {
                        if (!Objects.equals(presentCardId, latestCardIds.get(i))) {
                            cardId = latestCardIds.get(i);
                            return cardId;
                        }
                    }
                    return null;
                }
            } else {
                return cardId;
            }

        }
    }

    @Override
    @Transactional
    public Integer addCard(CardDTO cardDTO, Boolean needHistoryAndSync) {

        int type = cardDTO.getType();
        Object content = cardDTO.getContent();
        int returnId = -1;
        if (type == CardType.LITERATURE) {
            Literature literature = JSON.parseObject(JSON.toJSONString(content), Literature.class);
            literature.setDeleted(0);
            literatureMapper.insertLiterature(literature);
            returnId = literature.getId();

        } else if (type == CardType.SONG) {
            Song song = JSON.parseObject(JSON.toJSONString(content), Song.class);
            song.setDeleted(0);
            songMapper.insertSong(song);
            returnId = song.getId();

        } else if (type == CardType.WORDS) {
            Words words = JSON.parseObject(JSON.toJSONString(content), Words.class);
            words.setDeleted(0);
            wordsMapper.insertWords(words);
            returnId = words.getId();

        } else {
            assert false : "unknown card type error";
        }

        Card card = Card.builder()
                .type(type)
                .contentId(returnId)
                .deleted(0)
                .build();

        cardMapper.insertCard(card);

        if (needHistoryAndSync) {
            History history = History.builder()
                    .cardId(card.getId())
                    .type(HistoryType.ADD)
                    .build();
            historyMapper.insert2Tail(history);

            // sync to GitHub
            // note: need returned cardId
            cardDTO.setId(card.getId());
            cardSyncService.uploadCard(cardDTO, 0);

        }

        return returnId;

    }

    @Override
    public List<Integer> getAllCardIds() {
        List<Integer> cardIds = cardMapper.getAllCardIds();
        return cardIds;
    }

    @Override
    public List<Integer> searchCardByWord(String keyword) {
        /**
         * all types of cards
         */

        List<Integer> searchedCardIds = new ArrayList<Integer>();

        List<Integer> contentIds = cardMapper.getLiteratureIdsByWord(keyword);
        searchedCardIds.addAll(
            typeAndContentIds2CardIds(CardType.LITERATURE, contentIds)
        );

        contentIds = cardMapper.getSongIdsByWord(keyword);
        searchedCardIds.addAll(
            typeAndContentIds2CardIds(CardType.SONG, contentIds)
        );

        contentIds = cardMapper.getWordsIdsByWord(keyword);
        searchedCardIds.addAll(
            typeAndContentIds2CardIds(CardType.WORDS, contentIds)
        );

        return searchedCardIds;
    }


    public List<Integer> typeAndContentIds2CardIds(Integer type, List<Integer> contentIds) {
        List<Integer> cardIds = new ArrayList<Integer>();
        for (Integer contentId : contentIds) {
            cardIds.add(
                cardMapper.getCardIdByTypeAndContentId(type, contentId)
            );
        }
        return cardIds;
    }

    @Override
    public List<String> searchCardFieldByWord(String cardTypeName, String cardFieldName, String keyword) {

        List<String> fields = cardMapper.selectCardFieldByWord(cardTypeName, cardFieldName, keyword);

        return fields;
    }

    @Override
    public List<String> latestCardField(String cardTypeName, String cardFieldName, Integer number) {

        Integer total = cardMapper.getTypeTotalNumber(cardTypeName);

        Integer beginIndex = total - number;
        Integer offset = number;

        List<String> fields = cardMapper.getFieldsUsingLimit(cardTypeName, cardFieldName, beginIndex, offset);

        return fields;
    }

    @Override
    @Transactional
    public void deleteCard(Integer cardId, Boolean needHistoryAndSync) {

        Card card = cardMapper.getCardById(cardId);
        if (card == null) {
            System.out.println("Card may have been deleted!");
            return;
        }
        card.setUpdateTime(LocalDateTime.now().toString());
        cardMapper.deleteCardByCardId(card);

        if (Objects.equals(card.getType(), CardType.LITERATURE)) {
            Literature literature = Literature.builder()
                    .id(card.getContentId())
                    .updateTime(LocalDateTime.now().toString())
                    .build();
            literatureMapper.deleteLiteratureById(literature);
        } else if (Objects.equals(card.getType(), CardType.SONG)) {
            Song song = Song.builder()
                    .id(card.getContentId())
                    .updateTime(LocalDateTime.now().toString())
                    .build();
            songMapper.deleteSongById(song);
        } else if (Objects.equals(card.getType(), CardType.WORDS)) {
            Words words = Words.builder()
                    .id(card.getContentId())
                    .updateTime(LocalDateTime.now().toString())
                    .build();
            wordsMapper.deleteWordsById(words);
        } else {
            System.out.println("error cardType !");
        }

        if (needHistoryAndSync) {
            History history = History.builder().
                    cardId(cardId).
                    type(HistoryType.DELETE).
                    build();

            historyMapper.insert2Tail(history);

            // sync to GitHub
            cardSyncService.deleteCard(cardId);

        }
    }

    @Override
    @Transactional
    public void updateCard(CardDTO cardDTO, Boolean needHistoryAndSync) {

        // backup card(add card) and set deleted = 1(delete card) but both do not record histories
        Integer cardId = cardDTO.getId();
        CardVO oldCardVO = this.getCard(cardId);
        CardDTO oldCardDTO = CardDTO.builder().build();
        BeanUtils.copyProperties(oldCardVO, oldCardDTO);

        Integer returnCardId = this.addCard(oldCardDTO, false);

        this.deleteCard(returnCardId, false);

        // the card table only need to update update_time
        Card card = Card.builder()
                .id(cardId)
                .build();
        cardMapper.updateCardUpdateTime(card);

        // update card
        ObjectMapper mapper = new ObjectMapper();

        if (Objects.equals(cardDTO.getType(), CardType.LITERATURE)) {
            Literature oldLiterature = mapper.convertValue(oldCardDTO.getContent(), Literature.class);
            Literature literature = mapper.convertValue(cardDTO.getContent(), Literature.class);
            literature.setId(oldLiterature.getId());
            literatureMapper.updateLiterature(literature);
        } else if (Objects.equals(cardDTO.getType(), CardType.SONG)) {
            Song oldSong = mapper.convertValue(oldCardDTO.getContent(), Song.class);
            Song song = mapper.convertValue(cardDTO.getContent(), Song.class);
            song.setId(oldSong.getId());
            songMapper.updateSong(song);
        } else if (Objects.equals(cardDTO.getType(), CardType.WORDS)) {
            Words oldWords = mapper.convertValue(oldCardDTO.getContent(), Words.class);
            Words words = mapper.convertValue(cardDTO.getContent(), Words.class);
            words.setId(oldWords.getId());
            wordsMapper.updateWords(words);
        } else {
            System.out.println("error cardType !");
        }

        if (needHistoryAndSync) {
            // add updateHistory
            History history = History.builder()
                    .cardId(cardId)
                    .type(HistoryType.UPDATE)
                    .build();
            historyMapper.insert2Tail(history);

            cardSyncService.updateCard(cardDTO);
        }

    }

}
