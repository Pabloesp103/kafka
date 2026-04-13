package com.broker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "retry_jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RetryJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String jobType; // PAYMENT, ORDER, PRODUCT

    @Column(columnDefinition = "TEXT")
    private String data; // JSON string with the original payload

    private String statusA; // SUCCESS, FAILED, PENDING
    private String statusB; // SUCCESS, FAILED, PENDING
    private String statusC; // SUCCESS, FAILED, PENDING

    private String finalStatus; // SUCCESS, FAILED, PENDING

    private String errorMessage;

    private LocalDateTime createdAt;
    private LocalDateTime lastAttempt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (finalStatus == null) finalStatus = "PENDING";
        if (statusA == null) statusA = "PENDING";
        if (statusB == null) statusB = "PENDING";
        if (statusC == null) statusC = "PENDING";
    }
}
