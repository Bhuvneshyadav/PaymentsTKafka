package com.example.kDemo.service;

import com.example.kDemo.model.PaymentEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class DeadLetterService {

    @KafkaListener(topics = "${app.topics.payment-dlq}", groupId = "dlq-group")
    public void handleDeadLetter(ConsumerRecord<String, PaymentEvent> record) {
        PaymentEvent event = record.value();
        System.out.println("[DLQ] Received invalid payment event: " + event);
        // Here you could store to DB, send alert, etc.
    }
}
