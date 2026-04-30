package com.broker.service;

import com.broker.chain.StepAHandler;
import com.broker.chain.StepBHandler;
import com.broker.chain.StepCHandler;
import com.broker.model.BaseRetryJob;
import com.broker.model.OrderRetryJob;
import com.broker.model.PaymentRetryJob;
import com.broker.model.ProductRetryJob;
import com.broker.repository.OrderRetryJobRepository;
import com.broker.repository.PaymentRetryJobRepository;
import com.broker.repository.ProductRetryJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RetryScheduler {

    private static final Logger log = LoggerFactory.getLogger(RetryScheduler.class);

    private final PaymentRetryJobRepository paymentRepository;
    private final OrderRetryJobRepository orderRepository;
    private final ProductRetryJobRepository productRepository;
    
    private final StepAHandler stepA;
    private final StepBHandler stepB;
    private final StepCHandler stepC;

    public RetryScheduler(PaymentRetryJobRepository paymentRepository,
                          OrderRetryJobRepository orderRepository,
                          ProductRetryJobRepository productRepository,
                          StepAHandler stepA,
                          StepBHandler stepB,
                          StepCHandler stepC) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.stepA = stepA;
        this.stepB = stepB;
        this.stepC = stepC;
    }

    @Scheduled(fixedRate = 10000)
    public void processPaymentRetryJobs() {
        List<PaymentRetryJob> pendingJobs = paymentRepository.findByFinalStatus("PENDING");
        if (!pendingJobs.isEmpty()) {
            log.info("Processing {} pending PAYMENT retry jobs", pendingJobs.size());
            processJobs(pendingJobs);
            paymentRepository.saveAll(pendingJobs);
        }
    }

    @Scheduled(fixedRate = 10000)
    public void processOrderRetryJobs() {
        List<OrderRetryJob> pendingJobs = orderRepository.findByFinalStatus("PENDING");
        if (!pendingJobs.isEmpty()) {
            log.info("Processing {} pending ORDER retry jobs", pendingJobs.size());
            processJobs(pendingJobs);
            orderRepository.saveAll(pendingJobs);
        }
    }

    @Scheduled(fixedRate = 10000)
    public void processProductRetryJobs() {
        List<ProductRetryJob> pendingJobs = productRepository.findByFinalStatus("PENDING");
        if (!pendingJobs.isEmpty()) {
            log.info("Processing {} pending PRODUCT retry jobs", pendingJobs.size());
            processJobs(pendingJobs);
            productRepository.saveAll(pendingJobs);
        }
    }

    private void processJobs(List<? extends BaseRetryJob> jobs) {
        stepA.setNext(stepB);
        stepB.setNext(stepC);
        
        for (BaseRetryJob job : jobs) {
            job.setLastAttempt(LocalDateTime.now());
            
            int currentAttempts = job.getAttempts() + 1;
            job.setAttempts(currentAttempts);
            
            log.info("Processing job ID: {} - Attempt: {}/5", job.getId(), currentAttempts);
            
            stepA.handle(job);
        }
    }
}
