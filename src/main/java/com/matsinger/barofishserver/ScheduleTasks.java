package com.matsinger.barofishserver;

import com.matsinger.barofishserver.deliver.application.DeliverService;
import com.matsinger.barofishserver.order.application.OrderService;
import com.matsinger.barofishserver.search.application.SearchKeywordCommandService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ScheduleTasks {

    private final SearchKeywordCommandService searchKeywordCommandService;
    private final DeliverService deliverService;
    private final OrderService orderService;

    @Scheduled(cron = "0 0 0 * * 1")
    public void SearchKeywordSchedule() {
        searchKeywordCommandService.resetRank();
    }

    @Scheduled(cron = "0 0 */1 * * *")
    public void refreshDeliverState() {
        deliverService.refreshOrderDeliverState();
    }

    @Scheduled(cron = "0 0 */1 * * *")
    public void autoFinalConfirmOrder() {
        orderService.automaticFinalConfirm();
    }
}
