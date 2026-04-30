package com.broker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class BaseRetryJob {
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

    @Column(nullable = false, columnDefinition = "int default 0")
    private int attempts = 0; // Field to count retry attempts

    private LocalDateTime createdAt;
    private LocalDateTime lastAttempt;

    public BaseRetryJob() {}

    public BaseRetryJob(Long id, String jobType, String data, String statusA, String statusB, String statusC, String finalStatus, String errorMessage, int attempts, LocalDateTime createdAt, LocalDateTime lastAttempt) {
        this.id = id;
        this.jobType = jobType;
        this.data = data;
        this.statusA = statusA;
        this.statusB = statusB;
        this.statusC = statusC;
        this.finalStatus = finalStatus;
        this.errorMessage = errorMessage;
        this.attempts = attempts;
        this.createdAt = createdAt;
        this.lastAttempt = lastAttempt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (finalStatus == null) finalStatus = "PENDING";
        if (statusA == null) statusA = "PENDING";
        if (statusB == null) statusB = "PENDING";
        if (statusC == null) statusC = "PENDING";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    public String getStatusA() { return statusA; }
    public void setStatusA(String statusA) { this.statusA = statusA; }
    public String getStatusB() { return statusB; }
    public void setStatusB(String statusB) { this.statusB = statusB; }
    public String getStatusC() { return statusC; }
    public void setStatusC(String statusC) { this.statusC = statusC; }
    public String getFinalStatus() { return finalStatus; }
    public void setFinalStatus(String finalStatus) { this.finalStatus = finalStatus; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) { this.attempts = attempts; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getLastAttempt() { return lastAttempt; }
    public void setLastAttempt(LocalDateTime lastAttempt) { this.lastAttempt = lastAttempt; }
}
