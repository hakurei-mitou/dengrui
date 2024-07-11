package com.mitou.dengrui.mapper;

import com.mitou.dengrui.annotation.AutoFill;
import com.mitou.dengrui.enumeration.OperationType;
import com.mitou.dengrui.pojo.entity.History;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HistoryMapper {


    @Select("select count(*) from history ")
    Integer getTotalNumber();

    @Select("select * from history limit #{beginIndex}, #{offset}")
    List<History> getHistoriesUsingLimit(Integer beginIndex, Integer offset);


    @AutoFill(value = OperationType.HISTORY)
    @Insert("insert into history values (null, #{cardId}, #{type}, #{createTime})")
    void insert2Tail(History history);

    @Select("select cardId from history where type = #{type} ")
    List<Integer> getTypeCardIds(Integer type);

    @Select("select cardId from history")
    List<Integer> getAllCardIds();
}
