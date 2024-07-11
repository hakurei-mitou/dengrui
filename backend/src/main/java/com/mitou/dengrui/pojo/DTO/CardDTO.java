package com.mitou.dengrui.pojo.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardDTO {
    Integer id;
    Integer type;

    Object content;
}
