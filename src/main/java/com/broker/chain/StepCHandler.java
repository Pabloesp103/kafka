package com.broker.chain;

import com.broker.model.BaseRetryJob;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StepCHandler implements RetryHandler {
    private static final Logger log = LoggerFactory.getLogger(StepCHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void setNext(RetryHandler next) {
        // Last step
    }

    @Override
    public void handle(BaseRetryJob job) {
        log.info("Executing Step C (Final Status Update) for job ID: {}", job.getId());
        
        try {
            boolean isAllSuccess = "SUCCESS".equals(job.getStatusA()) && "SUCCESS".equals(job.getStatusB());
            String finalStatus = isAllSuccess ? "SUCCESS" : "FAILED";
            String finalMessage = isAllSuccess ? "Job completed successfully" : "Job failed in previous steps";

            job.setFinalStatus(finalStatus);
            job.setStatusC("SUCCESS");

            ObjectNode rootNode = (ObjectNode) objectMapper.readTree(job.getData());
            ObjectNode updateNode = (ObjectNode) rootNode.path("updateRetryJobs");
            if (updateNode.isMissingNode()) {
                updateNode = rootNode.putObject("updateRetryJobs");
            }
            updateNode.put("status", finalStatus);
            updateNode.put("message", finalMessage);
            
            job.setData(objectMapper.writeValueAsString(rootNode));

        } catch (Exception e) {
            job.setStatusC("FAILED");
            job.setFinalStatus("FAILED");
            log.error("Step C failed for job ID: {}. Error: {}", job.getId(), e.getMessage());
        }
        
        log.info("Job ID {} finished with status: {}", job.getId(), job.getFinalStatus());
    }
}
