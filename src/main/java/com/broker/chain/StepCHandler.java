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
            boolean isStepASuccess = "SUCCESS".equals(job.getStatusA());
            boolean isStepBSuccess = "SUCCESS".equals(job.getStatusB());
            
            String finalStatus;
            String finalMessage;

            if (isStepASuccess && isStepBSuccess) {
                finalStatus = "SUCCESS";
                finalMessage = "Job completed successfully";
                job.setStatusC("SUCCESS");
            } else if (!isStepASuccess) {
                // If Step A failed, we keep it PENDING to allow RetryScheduler to pick it up again
                log.info("Step A failed for job ID: {}. Keeping status PENDING for retry.", job.getId());
                finalStatus = "PENDING";
                finalMessage = "Step A failed: " + job.getErrorMessage();
                job.setStatusC("FAILED"); // Step C itself didn't fail, but the process is not successful yet
            } else {
                // Step A succeeded but Step B failed
                finalStatus = "FAILED";
                finalMessage = "Step A succeeded but Step B failed";
                job.setStatusC("FAILED");
            }

            job.setFinalStatus(finalStatus);

            ObjectNode rootNode = (ObjectNode) objectMapper.readTree(job.getData());
            ObjectNode updateNode = (ObjectNode) rootNode.path("updateRetryJobs");
            if (updateNode.isMissingNode() || updateNode.isNull()) {
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
