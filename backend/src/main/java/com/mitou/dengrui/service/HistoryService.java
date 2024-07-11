package com.mitou.dengrui.service;

import com.mitou.dengrui.pojo.VO.CardVO;
import io.swagger.v3.oas.models.security.SecurityScheme;

import java.util.List;

public interface HistoryService {

    List<Integer> getLatestHisotryCardIds(Integer latestNumber);

    void push2History(Integer cardId, Integer type);

    List<Integer> getLatestReadInAllCardIds(Integer latestNumber);
}
