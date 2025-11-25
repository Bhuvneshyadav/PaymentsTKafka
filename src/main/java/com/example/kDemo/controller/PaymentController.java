package com.example.kDemo.controller;


import com.example.kDemo.model.PaymentEvent;
import com.example.kDemo.model.PaymentStatus;
import com.example.kDemo.service.PaymentProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentProducer paymentProducer;

    @PostMapping
    public ResponseEntity<PaymentEvent> initiatePayment(@RequestParam String userId,
                                                        @RequestParam BigDecimal amount,
                                                        @RequestParam(defaultValue = "CARD") String method) {
        PaymentEvent event = PaymentEvent.builder()
                .paymentId(UUID.randomUUID().toString())
                .userId(userId)
                .amount(amount)
                .method(method)
                .status(PaymentStatus.INITIATED)
                .createdAt(Instant.now())
                .build();

        paymentProducer.publishPaymentInitiated(event);
        return ResponseEntity.ok(event);
    }
}
