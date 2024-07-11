package com.mitou.dengrui.mapper;

import com.mitou.dengrui.annotation.AutoFill;
import com.mitou.dengrui.enumeration.OperationType;
import com.mitou.dengrui.pojo.entity.Song;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SongMapper {

    @AutoFill(value = OperationType.INSERT)
    void insertSong(Song song);

    @Select("select * from song where id = #{id} and deleted = 0")
    Song getSongById(int id);

    @AutoFill(value = OperationType.UPDATE)
    @Update("update song set deleted = 1, update_time = #{updateTime} where id = #{id}")
    void deleteSongById(Song song);

    @AutoFill(value = OperationType.UPDATE)
    void updateSong(Song song);
}
