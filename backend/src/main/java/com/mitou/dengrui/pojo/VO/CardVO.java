package com.mitou.dengrui.pojo.VO;

import com.mitou.dengrui.pojo.entity.Literature;
import com.mitou.dengrui.pojo.entity.Song;
import com.mitou.dengrui.pojo.entity.Words;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardVO {
    Integer id;
    Integer type;
//  content:  Literature, Song, Words
    Object content;
}
