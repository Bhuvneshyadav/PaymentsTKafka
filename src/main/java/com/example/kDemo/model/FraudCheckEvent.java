package com.example.kDemo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudCheckEvent {

    private String paymentId;
    private String userId;
    private double fraudScore;    // 0 - 100
    private boolean fraud;
}
