package com.broker.chain;

import com.broker.model.RetryJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StepCHandler implements RetryHandler {
    
    @Override
    public void setNext(RetryHandler next) {
        // Last step
    }

    @Override
    public void handle(RetryJob job) {
        log.info("Executing Step C (Final Status Update) for job ID: {}", job.getId());
        
        if ("SUCCESS".equals(job.getStatusA()) && "SUCCESS".equals(job.getStatusB())) {
            job.setStatusC("SUCCESS");
            job.setFinalStatus("SUCCESS");
        } else {
            job.setStatusC("FAILED");
            job.setFinalStatus("FAILED");
        }
        log.info("Job ID {} finished with status: {}", job.getId(), job.getFinalStatus());
    }
}
