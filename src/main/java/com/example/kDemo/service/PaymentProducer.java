package com.example.kDemo.service;


import com.example.kDemo.model.PaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.topics.payment-initiated}")
    private String paymentInitiatedTopic;

    public void publishPaymentInitiated(PaymentEvent event) {
        kafkaTemplate.send(paymentInitiatedTopic, event.getPaymentId(), event);
        System.out.println("[PaymentProducer] Published payment-initiated: " + event);
    }
}
