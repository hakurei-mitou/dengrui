package com.mitou.dengrui.service;

import com.alibaba.fastjson.JSON;
import com.mitou.dengrui.mapper.CardMapper;
import com.mitou.dengrui.pojo.DTO.CardDTO;
import com.mitou.dengrui.pojo.VO.CardVO;
import com.mitou.dengrui.pojo.entity.Card;
import com.mitou.dengrui.utils.GitHubFileManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CardSyncService {

//    static final String localTempFilePath = "src/main/resources/db/temp.json";
    // use below after packaging
    static final String localTempFilePath = "classes/db/temp.json";
    static final String filenameSeparator = ".";
    static final String separatorPattern = "\\" + filenameSeparator;

    @Autowired
    private GitHubFileManager gitHubFileManager;

    private static void showSyncErrorMessage() {
        System.out.println("This sync failed! Auto sync will launch when everytime entering this APP.");
    }

    private String dateTime2String(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter).replace(' ', 'T');
    }

    private LocalDateTime string2DateTime(String string) {
        string = string.replace(" ", "T");
        return LocalDateTime.parse(string, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private String generateJsonFileName(Integer cardId, Integer deleted) {
        Card card = cardMapper.getCardById(cardId);
        LocalDateTime localDateTime = string2DateTime(card.getUpdateTime());
        String stringDatetime = dateTime2String(localDateTime);
        return cardId + filenameSeparator + stringDatetime + filenameSeparator + deleted + ".json";
    }

    public void uploadCard(CardDTO cardDTO, Integer deletedFlag) {

        String filename = generateJsonFileName(cardDTO.getId(), deletedFlag);

        // write to file
        String jsonString = JSON.toJSONString(cardDTO);
        try {
            Files.writeString(Path.of(localTempFilePath), jsonString);
            gitHubFileManager.uploadFile(localTempFilePath, GitHubFileManager.cardDataDirName + "/" + filename);
        } catch (IOException e) {
            showSyncErrorMessage();
        }

    }

    private Integer extractCardId(String jsonFilename) {
        String string = jsonFilename.split(separatorPattern)[0];
        return Integer.parseInt(string);
    }
    private LocalDateTime extractDatetime(String jsonFilename) {
        String string = jsonFilename.split(separatorPattern)[1];
        return string2DateTime(string);
    }
    private Integer extractDeletedFlag(String jsonFilename) {
        String string = jsonFilename.split(separatorPattern)[2];
        return Integer.parseInt(string);
    }

    private String getLatestFilenameByCardId(Integer cardId) {
        List<String> nameList = gitHubFileManager.getPathNameList(GitHubFileManager.cardDataDirName);
        assert nameList != null;
        List<String> multipleVersionCardJsonFilename = nameList.stream()
                .filter(e -> extractCardId(e).equals(cardId))
                // ascending order by default
                .sorted()
                .toList();
        return multipleVersionCardJsonFilename.get(multipleVersionCardJsonFilename.size() - 1);
    }

    public void deleteCard(Integer cardId) {
        // download the card json
        CardDTO cardDTO = getCard(cardId);
        if (cardDTO == null) {
            System.out.println("No this card!");
            return;
        }

        // logistically delete the latest card (upload a new filename with deleted flag = 1)
        uploadCard(cardDTO, 1);
    }

    public CardDTO getCard(Integer cardId) {
        String latestName = getLatestFilenameByCardId(cardId);
        if (extractDeletedFlag(latestName) == 1) {
            return null;
        }

        CardDTO cardDTO = null;
        try {
            gitHubFileManager.downloadFile(GitHubFileManager.cardDataDirName + "/" + latestName, localTempFilePath);
            cardDTO = JSON.parseObject(Files.readString(Path.of(localTempFilePath)), CardDTO.class);
        } catch (IOException e) {
            showSyncErrorMessage();
        }

        return cardDTO;
    }

    public void updateCard(CardDTO cardDTO) {
        uploadCard(cardDTO, 0);
    }


    @Autowired
    CardMapper cardMapper;


    public void syncSQLiteAndGitHub(CardService cardService) {

        @Data
        @AllArgsConstructor
        class SyncEntry {
            Integer id;
            LocalDateTime dateTime;
            Integer deleted;
            Boolean needUpdate;
        }

        // get all (cardId, update_time, deleted) from SQLite
        List<Card> cards = cardMapper.getAllCard();
        Map<Integer, SyncEntry> sQLiteEntriesMap = cards.stream()
                .map(card -> new SyncEntry(
                        card.getId(),
                        string2DateTime(card.getUpdateTime()),
                        card.getDeleted(),
                        false
                ))
                .collect(Collectors.toMap(
                        SyncEntry::getId,
                        e -> e
                        // cards are always unique.
//                        (a, b) ->  a.dateTime.isAfter(b.dateTime) ? a : b
                ));


        // get all (cardId, datetime, deleted_flag) from GitHub
        List<String> names = gitHubFileManager.getPathNameList(GitHubFileManager.cardDataDirName);
        if (names == null) {
            System.out.println("cardDataDirName dir has not been created!");
            return;
        }
        Map<Integer, SyncEntry> gitHubEntriesMap = names.stream()
                .map(name -> new SyncEntry(
                        extractCardId(name),
                        extractDatetime(name),
                        extractDeletedFlag(name),
                        false
                ))
                .collect(Collectors.toMap(
                        SyncEntry::getId,
                        e -> e,
                        (a, b) ->  a.dateTime.isAfter(b.dateTime) ? a : b
                ));


        // filter entries.
        // the sizes of sQLiteEntriesMap and gitHubEntriesMap are always little difference.
        Iterator<Map.Entry<Integer, SyncEntry>> iterator = sQLiteEntriesMap.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<Integer, SyncEntry> entry = iterator.next();
            Integer id = entry.getKey();
            SyncEntry sQLiteEntry = entry.getValue();

            if (gitHubEntriesMap.containsKey(id)) {
                SyncEntry gitHubEntry = gitHubEntriesMap.get(id);
                long seconds = Duration.between(sQLiteEntry.dateTime, gitHubEntry.dateTime).getSeconds();
                if (Math.abs(seconds) <= 10) {
                    iterator.remove();
                    gitHubEntriesMap.remove(id);
                } else {
                    if (sQLiteEntry.dateTime.isAfter(gitHubEntry.dateTime)) {
                        gitHubEntriesMap.remove(id);
                        sQLiteEntry.setNeedUpdate(true);
                    } else {
                        iterator.remove();
                        gitHubEntry.setNeedUpdate(true);
                    }
                }
            }
        }

        System.out.println("### sQLiteEntriesMap = \n" + sQLiteEntriesMap + "\n gitHubEntriesMap = \n" + gitHubEntriesMap);

        // update SQLite from GitHub
        if (!gitHubEntriesMap.isEmpty()) {
            gitHubEntriesMap.forEach((id, gitHubEntry) -> {
                // 'deleted flag' is prioritized to 'update flag'.
                if (gitHubEntry.deleted == 1) {
                    // delete card
                    cardService.deleteCard(gitHubEntry.getId(), false);
                } else {
                    CardDTO cardDTO = getCard(gitHubEntry.getId());
                    if (gitHubEntry.needUpdate) {
                        // update card
                        cardService.updateCard(cardDTO, false);
                    } else {
                        // add card
                        cardService.addCard(cardDTO, false);
                    }
                }
            });
        }


        // update GitHub from SQLite
        if (!sQLiteEntriesMap.isEmpty()) {
            sQLiteEntriesMap.forEach((id, sQLiteEntry) -> {
                if (sQLiteEntry.deleted == 1) {
                    // delete card
                    deleteCard(sQLiteEntry.getId());
                } else {
                    CardVO cardVO = cardService.getCard(sQLiteEntry.getId());
                    CardDTO cardDTO = CardDTO.builder()
                            .id(cardVO.getId())
                            .type(cardVO.getType())
                            .content(cardVO.getContent())
                            .build();
                    if (sQLiteEntry.needUpdate) {
                        // update card
                        updateCard(cardDTO);
                    } else {
                        // add card
                        uploadCard(cardDTO,0);
                    }
                }
            });
        }


    }

}