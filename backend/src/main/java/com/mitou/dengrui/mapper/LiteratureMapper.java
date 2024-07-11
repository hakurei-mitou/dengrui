package com.mitou.dengrui.mapper;

import com.mitou.dengrui.annotation.AutoFill;
import com.mitou.dengrui.enumeration.OperationType;
import com.mitou.dengrui.pojo.entity.Literature;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LiteratureMapper {

    @Select("select * from literature where id = #{id} and deleted = 0")
    Literature getLiteratureById(Integer id);

    @AutoFill(value = OperationType.INSERT)
    void insertLiterature(Literature literature);


    @AutoFill(value = OperationType.UPDATE)
    @Update("update literature set deleted = 1, update_time = #{updateTime} where id = #{id}")
    void deleteLiteratureById(Literature literature);

    @AutoFill(value = OperationType.UPDATE)
    void updateLiterature(Literature literature);
}
