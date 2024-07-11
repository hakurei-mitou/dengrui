package com.mitou.dengrui.pojo.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class History {
    Integer id;
    Integer cardId;
    Integer type;
    String createTime;
}
