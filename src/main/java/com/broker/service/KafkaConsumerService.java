package com.broker.service;

import com.broker.model.RetryJob;
import com.broker.repository.RetryJobRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final RetryJobRepository repository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "payments_retry_jobs", groupId = "broker-group")
    public void consumePaymentRetry(Map<String, Object> payload) {
        saveJob("PAYMENT", payload);
    }

    @KafkaListener(topics = "order_retry_jobs", groupId = "broker-group")
    public void consumeOrderRetry(Map<String, Object> payload) {
        saveJob("ORDER", payload);
    }

    @KafkaListener(topics = "product_retry_jobs", groupId = "broker-group")
    public void consumeProductRetry(Map<String, Object> payload) {
        saveJob("PRODUCT", payload);
    }

    private void saveJob(String type, Map<String, Object> payload) {
        try {
            log.info("Received retry job for type {}: {}", type, payload);
            RetryJob job = RetryJob.builder()
                    .jobType(type)
                    .data(objectMapper.writeValueAsString(payload))
                    .finalStatus("PENDING")
                    .build();
            repository.save(job);
            log.info("Saved job {} to database", type);
        } catch (JsonProcessingException e) {
            log.error("Error serializing payload for {}", type, e);
        }
    }
}
