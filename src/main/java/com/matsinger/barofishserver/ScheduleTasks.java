package com.matsinger.barofishserver;

import com.matsinger.barofishserver.search.SearchKeywordService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ScheduleTasks {

    private final SearchKeywordService searchKeywordService;

    @Scheduled(cron = "0 0 0 * * 1")
    public void SearchKeywordSchedule(){
        searchKeywordService.resetRank();
    }
}
