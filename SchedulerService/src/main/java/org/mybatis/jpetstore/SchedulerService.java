package org.mybatis.jpetstore;

import org.mybatis.jpetstore.DTO.PendingOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class SchedulerService {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public SchedulerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics="pending_order_schedule", groupId = "group_3")
    public void scheduledOrder(PendingOrder data) {
        scheduler.schedule(() -> {
            sendPendingMessage(data);
        }, data.getPendingTime() * 15, TimeUnit.SECONDS);
    }

    public void sendPendingMessage(PendingOrder data) {
        kafkaTemplate.send("pending_order", data);
    }
}
