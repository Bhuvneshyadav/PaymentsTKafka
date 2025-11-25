package com.example.kDemo.service;


import com.example.kDemo.model.PaymentEvent;
import com.example.kDemo.model.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentValidationService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.topics.payment-validated}")
    private String paymentValidatedTopic;

    @Value("${app.topics.payment-dlq}")
    private String paymentDlqTopic;

    @KafkaListener(topics = "${app.topics.payment-initiated}", groupId = "validator-group")
    public void validatePayment(ConsumerRecord<String, PaymentEvent> record) {
        PaymentEvent event = record.value();
        System.out.println("[Validation] Received: " + event);

        if (event.getAmount() == null || event.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            event.setStatus(PaymentStatus.DECLINED);
            event.setReason("Invalid amount");
            kafkaTemplate.send(paymentDlqTopic, event.getPaymentId(), event);
            System.out.println("[Validation] Sent to DLQ: " + event);
            return;
        }

        // Additional checks (user id, method, etc.) can go here

        event.setStatus(PaymentStatus.VALIDATED);
        kafkaTemplate.send(paymentValidatedTopic, event.getPaymentId(), event);
        System.out.println("[Validation] Payment validated: " + event);
    }
}
