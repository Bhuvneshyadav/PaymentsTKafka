package com.example.kDemo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEvent {

    private String paymentId;
    private String userId;
    private BigDecimal amount;
    private String method;     // CARD, UPI, NETBANKING...
    private PaymentStatus status;
    private Instant createdAt;
    private String reason;     // Used for declined, validation error, etc.
}
