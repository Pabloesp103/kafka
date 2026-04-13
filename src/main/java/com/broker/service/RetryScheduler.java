package com.broker.service;

import com.broker.chain.StepAHandler;
import com.broker.chain.StepBHandler;
import com.broker.chain.StepCHandler;
import com.broker.model.RetryJob;
import com.broker.repository.RetryJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetryScheduler {

    private final RetryJobRepository repository;
    private final StepAHandler stepA;
    private final StepBHandler stepB;
    private final StepCHandler stepC;

    @Scheduled(fixedRate = 10000) // 10 seconds
    public void processRetryJobs() {
        List<RetryJob> pendingJobs = repository.findByFinalStatus("PENDING");
        
        if (!pendingJobs.isEmpty()) {
            log.info("Processing {} pending retry jobs", pendingJobs.size());
            
            // Configure chain
            stepA.setNext(stepB);
            stepB.setNext(stepC);
            
            for (RetryJob job : pendingJobs) {
                job.setLastAttempt(LocalDateTime.now());
                stepA.handle(job);
                repository.save(job);
            }
        }
    }
}
