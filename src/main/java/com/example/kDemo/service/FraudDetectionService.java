package com.example.kDemo.service;


import com.example.kDemo.model.FraudCheckEvent;
import com.example.kDemo.model.PaymentEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class FraudDetectionService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Random random = new Random();

    @Value("${app.topics.payment-fraud-check}")
    private String paymentFraudCheckTopic;

    @KafkaListener(topics = "${app.topics.payment-validated}", groupId = "fraud-group")
    public void checkFraud(ConsumerRecord<String, PaymentEvent> record) {
        PaymentEvent event = record.value();
        System.out.println("[Fraud] Checking payment: " + event);

        // Simple fake rule: random fraud score
        double score = random.nextDouble() * 100;
        boolean isFraud = score > 80.0; // mark as fraud if > 80

        FraudCheckEvent fraudEvent = FraudCheckEvent.builder()
                .paymentId(event.getPaymentId())
                .userId(event.getUserId())
                .fraudScore(score)
                .fraud(isFraud)
                .build();

        kafkaTemplate.send(paymentFraudCheckTopic, event.getPaymentId(), fraudEvent);
        System.out.println("[Fraud] Published fraud-check: " + fraudEvent);
    }
}

