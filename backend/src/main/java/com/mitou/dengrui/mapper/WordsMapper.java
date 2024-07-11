package com.mitou.dengrui.mapper;

import com.mitou.dengrui.annotation.AutoFill;
import com.mitou.dengrui.enumeration.OperationType;
import com.mitou.dengrui.pojo.entity.Words;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface WordsMapper {

    @Select("select * from words where id = #{id} and deleted = 0 ")
    Words getWordsById(int id);

    @AutoFill(value = OperationType.INSERT)
    void insertWords(Words words);

    @AutoFill(value = OperationType.UPDATE)
    @Update("update words set deleted = 1, update_time = #{updateTime} where id = #{id}")
    void deleteWordsById(Words words);

    @AutoFill(value = OperationType.UPDATE)
    void updateWords(Words words);

}
