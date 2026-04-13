package com.broker.chain;

import com.broker.model.RetryJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StepBHandler implements RetryHandler {
    private RetryHandler next;

    @Override
    public void setNext(RetryHandler next) {
        this.next = next;
    }

    @Override
    public void handle(RetryJob job) {
        log.info("Executing Step B (Send Email) for job ID: {}", job.getId());
        
        try {
            if ("SUCCESS".equals(job.getStatusA())) {
                log.info("Sending SUCCESS email for job ID: {}", job.getId());
                // Simulación de envío de correo exitoso
            } else {
                log.warn("Sending FAILURE email for job ID: {}", job.getId());
                // Simulación de envío de correo de fallo
            }
            job.setStatusB("SUCCESS");
        } catch (Exception e) {
            job.setStatusB("FAILED");
            log.error("Step B failed for job ID: {}", job.getId());
        }

        if (next != null) next.handle(job);
    }
}
