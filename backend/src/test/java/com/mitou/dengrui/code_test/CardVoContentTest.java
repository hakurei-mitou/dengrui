package com.mitou.dengrui.code_test;

import com.mitou.dengrui.pojo.VO.CardVO;
import com.mitou.dengrui.pojo.entity.Literature;
import com.mitou.dengrui.pojo.entity.Song;
import com.mitou.dengrui.pojo.entity.Words;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CardVoContentTest {

    @Test
    public void main() {

        Literature literature = Literature.builder().build();
        Song song = Song.builder().build();
        Words words = Words.builder().build();

        CardVO cardVOLitreature = CardVO.builder().content(literature).build();
        CardVO cardVOSong = CardVO.builder().content(song).build();

        System.out.println(cardVOLitreature);
        System.out.println(cardVOSong);

    }
}