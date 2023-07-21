package com.matsinger.barofishserver;

import com.matsinger.barofishserver.deliver.application.DeliverService;
import com.matsinger.barofishserver.search.application.SearchKeywordService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ScheduleTasks {

    private final SearchKeywordService searchKeywordService;
    private final DeliverService deliverService;

    @Scheduled(cron = "0 0 0 * * 1")
    public void SearchKeywordSchedule() {
        searchKeywordService.resetRank();
    }

    @Scheduled(cron = "0 0 */1 * * *")
    public void refreshDeliverState() {
        deliverService.refreshOrderDeliverState();
    }
}
