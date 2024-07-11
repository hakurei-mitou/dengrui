package com.mitou.dengrui.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Song {
    Integer id;
    String title;
    String translationTitle;
    String originalText;
    String translationText;
    String voice;
    String compose;
    String arrangement;
    String lyric;
    String album;
    String source;
    String note;
    String createTime;
    String updateTime;
    Integer deleted;
}
