package com.mitou.dengrui.mapper;

import com.mitou.dengrui.annotation.AutoFill;
import com.mitou.dengrui.enumeration.OperationType;
import com.mitou.dengrui.pojo.DTO.CardDTO;
import com.mitou.dengrui.pojo.entity.Card;
import com.mitou.dengrui.pojo.entity.Literature;
import com.mitou.dengrui.pojo.entity.Song;
import com.mitou.dengrui.pojo.entity.Words;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Collection;
import java.util.List;

@Mapper
public interface CardMapper {


    @Select("select * from card where id = #{id} and deleted = 0")
    Card getCardById(Integer id);

    @Select("select count(*) from card where deleted = 0")
    Integer getTotalNumber();

    @Select("select count(*) from ${cardTypeName} where deleted = 0")
    Integer getTypeTotalNumber(String cardTypeName);

    @AutoFill(value = OperationType.INSERT)
    void insertCard(Card card);

    @Select("select id from card where deleted = 0")
    List<Integer> getAllCardIds();

    @Select("select id from literature where deleted = 0 and ( " +
            "instr(lower(title), lower(#{keyword})) or " +
            "instr(lower(translation_title), lower(#{keyword})) or " +
            "instr(lower(original_text), lower(#{keyword})) or " +
            "instr(lower(translation_text), lower(#{keyword})) or " +
            "instr(lower(author), lower(#{keyword})) or " +
            "instr(lower(translator), lower(#{keyword})) or " +
            "instr(lower(book), lower(#{keyword})) or " +
            "instr(lower(edition), lower(#{keyword})) or " +
            "instr(lower(note), lower(#{keyword})) " +
            " )" )
    List<Integer> getLiteratureIdsByWord(String keyword);

    @Select("select id from song where deleted = 0 and ( " +
            "instr(lower(title), lower(#{keyword})) or " +
            "instr(lower(translation_title), lower(#{keyword})) or " +
            "instr(lower(original_text), lower(#{keyword})) or " +
            "instr(lower(translation_text), lower(#{keyword})) or " +
            "instr(lower(lyric), lower(#{keyword})) or " +
            "instr(lower(voice), lower(#{keyword})) or " +
            "instr(lower(compose), lower(#{keyword})) or " +
            "instr(lower(arrangement), lower(#{keyword})) or " +
            "instr(lower(album), lower(#{keyword})) or " +
            "instr(lower(source), lower(#{keyword})) or " +
            "instr(lower(note), lower(#{keyword})) " +
            " )" )
    List<Integer> getSongIdsByWord(String keyword);

    @Select("select id from words where deleted = 0 and ( " +
            "instr(lower(original_text), lower(#{keyword})) or " +
            "instr(lower(translation_text), lower(#{keyword})) or " +
            "instr(lower(sayer), lower(#{keyword})) or " +
            "instr(lower(translator), lower(#{keyword})) or " +
            "instr(lower(source), lower(#{keyword})) or " +
            "instr(lower(note), lower(#{keyword})) " +
            " )" )
    List<Integer> getWordsIdsByWord(String keyword);

    @Select("select id from card where type = #{literature} and content_id = #{contentId} and deleted = 0")
    Integer getCardIdByTypeAndContentId(Integer literature, Integer contentId);

    @Select("select ${cardFieldName} from ${cardTypeName} " +
            "where deleted = 0 and instr(lower(${cardFieldName}), lower(#{keyword})) " +
            "order by update_time desc")
    List<String> selectCardFieldByWord(String cardTypeName, String cardFieldName, String keyword);

    @Select("select ${cardFieldName} from ${cardTypeName} where ${cardFieldName} is not null and deleted = 0 " +
            "limit #{beginIndex}, #{offset}")
    List<String> getFieldsUsingLimit(String cardTypeName, String cardFieldName, Integer beginIndex, Integer offset);

    @AutoFill(value = OperationType.UPDATE)
    @Update("update card set deleted = 1, update_time = #{updateTime} where id = #{id}")
    void deleteCardByCardId(Card card);


    @AutoFill(value = OperationType.UPDATE)
    @Update("update card set update_time = #{updateTime} where id = #{id}")
    void updateCardUpdateTime(Card card);

    @Select("select id from card where deleted = 0 limit #{rank}, 1 ")
    Integer getCardIdByRank(Integer rank);

    /**
     * @return the return card carry deleted flag
     */
    @Select("select id, type, content_id, create_time, update_time, deleted from card")
    List<Card> getAllCard();
}