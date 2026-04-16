package com.broker.service;

import com.broker.model.OrderRetryJob;
import com.broker.model.PaymentRetryJob;
import com.broker.model.ProductRetryJob;
import com.broker.repository.OrderRetryJobRepository;
import com.broker.repository.PaymentRetryJobRepository;
import com.broker.repository.ProductRetryJobRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final PaymentRetryJobRepository paymentRepository;
    private final OrderRetryJobRepository orderRepository;
    private final ProductRetryJobRepository productRepository;
    private final ObjectMapper objectMapper;

    public KafkaConsumerService(PaymentRetryJobRepository paymentRepository, 
                                OrderRetryJobRepository orderRepository, 
                                ProductRetryJobRepository productRepository, 
                                ObjectMapper objectMapper) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "payments_retry_jobs", groupId = "broker-group")
    public void consumePaymentRetry(Map<String, Object> payload) {
        try {
            log.info("Received payment retry job: {}", payload);
            PaymentRetryJob job = new PaymentRetryJob();
            job.setJobType("PAYMENT");
            job.setData(objectMapper.writeValueAsString(payload));
            job.setFinalStatus("PENDING");
            paymentRepository.save(job);
            log.info("Saved Payment job to database");
        } catch (JsonProcessingException e) {
            log.error("Error serializing payment payload", e);
        }
    }

    @KafkaListener(topics = "order_retry_jobs", groupId = "broker-group")
    public void consumeOrderRetry(Map<String, Object> payload) {
        try {
            log.info("Received order retry job: {}", payload);
            OrderRetryJob job = new OrderRetryJob();
            job.setJobType("ORDER");
            job.setData(objectMapper.writeValueAsString(payload));
            job.setFinalStatus("PENDING");
            orderRepository.save(job);
            log.info("Saved Order job to database");
        } catch (JsonProcessingException e) {
            log.error("Error serializing order payload", e);
        }
    }

    @KafkaListener(topics = "product_retry_jobs", groupId = "broker-group")
    public void consumeProductRetry(Map<String, Object> payload) {
        try {
            log.info("Received product retry job: {}", payload);
            ProductRetryJob job = new ProductRetryJob();
            job.setJobType("PRODUCT");
            job.setData(objectMapper.writeValueAsString(payload));
            job.setFinalStatus("PENDING");
            productRepository.save(job);
            log.info("Saved Product job to database");
        } catch (JsonProcessingException e) {
            log.error("Error serializing product payload", e);
        }
    }
}
