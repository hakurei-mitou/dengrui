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
public class Literature {
    Integer id;
    String title;
    String translationTitle;
    String originalText;
    String translationText;
    String author;
    String translator;
    String book;
    String edition;
    String note;
    String createTime;
    String updateTime;
    Integer deleted;
}
