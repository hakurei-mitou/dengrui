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
public class Words {
    Integer id;
    String originalText;
    String translationText;
    String sayer;
    String translator;
    String source;
    String note;
    String createTime;
    String updateTime;
    Integer deleted;
}
