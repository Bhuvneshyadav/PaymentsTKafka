package com.example.kDemo.service;

import com.example.kDemo.model.FraudCheckEvent;
import com.example.kDemo.model.PaymentEvent;
import com.example.kDemo.model.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class PaymentProcessorService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Random random = new Random();

    @Value("${app.topics.payment-authorized}")
    private String paymentAuthorizedTopic;

    @Value("${app.topics.payment-declined}")
    private String paymentDeclinedTopic;

    // Cache validated payments in memory
    private final Map<String, PaymentEvent> validatedPayments = new ConcurrentHashMap<>();

    @KafkaListener(topics = "${app.topics.payment-validated}", groupId = "processor-cache-group")
    public void cacheValidatedPayment(ConsumerRecord<String, PaymentEvent> record) {
        PaymentEvent event = record.value();
        validatedPayments.put(event.getPaymentId(), event);
        System.out.println("[Processor] Cached validated payment: " + event);
    }

    @KafkaListener(topics = "${app.topics.payment-fraud-check}", groupId = "processor-group")
    public void processPayment(ConsumerRecord<String, FraudCheckEvent> record) {
        FraudCheckEvent fraudEvent = record.value();
        System.out.println("[Processor] Received fraud-check: " + fraudEvent);

        PaymentEvent payment = validatedPayments.get(fraudEvent.getPaymentId());
        if (payment == null) {
            System.out.println("[Processor] No payment found in cache for id " + fraudEvent.getPaymentId());
            return;
        }

        if (fraudEvent.isFraud()) {
            payment.setStatus(PaymentStatus.DECLINED);
            payment.setReason("Fraud detected: score=" + fraudEvent.getFraudScore());
            kafkaTemplate.send(paymentDeclinedTopic, payment.getPaymentId(), payment);
            System.out.println("[Processor] Payment declined (fraud): " + payment);
            return;
        }

        // Simulate external bank API call
        boolean externalApproved = random.nextDouble() > 0.1; // 90% success rate

        if (externalApproved) {
            payment.setStatus(PaymentStatus.AUTHORIZED);
            kafkaTemplate.send(paymentAuthorizedTopic, payment.getPaymentId(), payment);
            System.out.println("[Processor] Payment authorized: " + payment);
        } else {
            payment.setStatus(PaymentStatus.DECLINED);
            payment.setReason("Bank declined the transaction");
            kafkaTemplate.send(paymentDeclinedTopic, payment.getPaymentId(), payment);
            System.out.println("[Processor] Payment declined (bank): " + payment);
        }
    }
}
