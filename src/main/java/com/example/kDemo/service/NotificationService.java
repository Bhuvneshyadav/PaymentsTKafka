package com.example.kDemo.service;

import com.example.kDemo.model.PaymentEvent;
import com.example.kDemo.model.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.topics.payment-notifications}")
    private String paymentNotificationsTopic;

    @KafkaListener(topics = "${app.topics.payment-authorized}", groupId = "notification-group")
    public void handleAuthorized(ConsumerRecord<String, PaymentEvent> record) {
        PaymentEvent event = record.value();
        System.out.println("[Notification] Payment SUCCESS for user " + event.getUserId()
                + ", amount=" + event.getAmount());

        sendNotification(event);
    }

    @KafkaListener(topics = "${app.topics.payment-declined}", groupId = "notification-group")
    public void handleDeclined(ConsumerRecord<String, PaymentEvent> record) {
        PaymentEvent event = record.value();
        System.out.println("[Notification] Payment FAILED for user " + event.getUserId()
                + ", amount=" + event.getAmount()
                + ", reason=" + event.getReason());

        sendNotification(event);
    }

    private void sendNotification(PaymentEvent event) {
        // In real life: call SMS/email/push API.
        // Here: just publish to "payment-notifications" topic.
        kafkaTemplate.send(paymentNotificationsTopic, event.getPaymentId(), event);
        System.out.println("[Notification] Published notification event: " + event.getStatus());
    }
}
