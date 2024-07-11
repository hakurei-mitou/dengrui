package com.mitou.dengrui.pojo.entity;

import lombok.Builder;
import lombok.Data;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Builder
public class Card {
    Integer id;
    Integer type;
    Integer contentId;
    String createTime;
    String updateTime;
    Integer deleted;
}
