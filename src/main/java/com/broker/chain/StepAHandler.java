package com.broker.chain;

import com.broker.model.BaseRetryJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class StepAHandler implements RetryHandler {
    private static final Logger log = LoggerFactory.getLogger(StepAHandler.class);
    private RetryHandler next;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void setNext(RetryHandler next) {
        this.next = next;
    }

    @Override
    public void handle(BaseRetryJob job) {
        log.info("Executing Step A (Resend request) for job ID: {}", job.getId());
        String url = getUrlForJobType(job.getJobType());
        
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode rootNode = mapper.readTree(job.getData());
            com.fasterxml.jackson.databind.JsonNode dataNode = rootNode.path("data");
            
            String businessData = mapper.writeValueAsString(dataNode);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Retry-Attempt", "true");
            HttpEntity<String> request = new HttpEntity<>(businessData, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            com.fasterxml.jackson.databind.node.ObjectNode rootObject = (com.fasterxml.jackson.databind.node.ObjectNode) rootNode;
            com.fasterxml.jackson.databind.node.ObjectNode dataObject = (com.fasterxml.jackson.databind.node.ObjectNode) rootObject.path("data");

            if (response.getStatusCode().is2xxSuccessful()) {
                job.setStatusA("SUCCESS");
                dataObject.put("status", "SUCCESS");
                job.setData(mapper.writeValueAsString(rootObject));
                log.info("Step A successful for job ID: {}", job.getId());
                if (next != null) next.handle(job);
            } else {
                dataObject.put("status", "FAILED");
                job.setData(mapper.writeValueAsString(rootObject));
                fail(job, "Endpoint returned status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            fail(job, "Error calling endpoint: " + e.getMessage());
        }
    }

    private void fail(BaseRetryJob job, String message) {
        job.setStatusA("FAILED");
        job.setErrorMessage(message);
        log.error("Step A failed for job ID {}: {}", job.getId(), message);
        if (next != null) next.handle(job);
    }

    private String getUrlForJobType(String type) {
        return switch (type) {
            case "PRODUCT" -> "http://productservice:8081/productos";
            case "ORDER" -> "http://orderservice:8082/ordenes";
            case "PAYMENT" -> "http://paymentservice:8083/pagos/procesar";
            default -> throw new IllegalArgumentException("Unknown job type: " + type);
        };
    }
}
