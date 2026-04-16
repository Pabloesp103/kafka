package com.broker.controller;

import com.broker.model.BaseRetryJob;
import com.broker.repository.OrderRetryJobRepository;
import com.broker.repository.PaymentRetryJobRepository;
import com.broker.repository.ProductRetryJobRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/retry-jobs")
public class RetryMonitorController {

    private final PaymentRetryJobRepository paymentRepository;
    private final OrderRetryJobRepository orderRepository;
    private final ProductRetryJobRepository productRepository;

    public RetryMonitorController(PaymentRetryJobRepository paymentRepository,
                                  OrderRetryJobRepository orderRepository,
                                  ProductRetryJobRepository productRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @GetMapping("/status")
    public List<JobStatusDTO> getAllStatuses() {
        List<JobStatusDTO> allJobs = new ArrayList<>();
        
        allJobs.addAll(paymentRepository.findAll().stream()
                .map(job -> new JobStatusDTO(job, "PAYMENT")).collect(Collectors.toList()));
        
        allJobs.addAll(orderRepository.findAll().stream()
                .map(job -> new JobStatusDTO(job, "ORDER")).collect(Collectors.toList()));
        
        allJobs.addAll(productRepository.findAll().stream()
                .map(job -> new JobStatusDTO(job, "PRODUCT")).collect(Collectors.toList()));
        
        return allJobs;
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/clear")
    public String clearAllLogs() {
        paymentRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
        return "Dashboard limpiado exitosamente (3 tablas vaciadas)";
    }

    public static class JobStatusDTO {
        private Long id;
        private String type;
        private String statusA;
        private String statusB;
        private String statusC;
        private String finalStatus;
        private String lastAttempt;
        private String error;

        public JobStatusDTO() {}

        public JobStatusDTO(BaseRetryJob job, String type) {
            this.id = job.getId();
            this.type = type;
            this.statusA = job.getStatusA();
            this.statusB = job.getStatusB();
            this.statusC = job.getStatusC();
            this.finalStatus = job.getFinalStatus();
            this.lastAttempt = job.getLastAttempt() != null ? job.getLastAttempt().toString() : "N/A";
            this.error = job.getErrorMessage();
        }

        // Getters
        public Long getId() { return id; }
        public String getType() { return type; }
        public String getStatusA() { return statusA; }
        public String getStatusB() { return statusB; }
        public String getStatusC() { return statusC; }
        public String getFinalStatus() { return finalStatus; }
        public String getLastAttempt() { return lastAttempt; }
        public String getError() { return error; }

        // Setters
        public void setId(Long id) { this.id = id; }
        public void setType(String type) { this.type = type; }
        public void setStatusA(String statusA) { this.statusA = statusA; }
        public void setStatusB(String statusB) { this.statusB = statusB; }
        public void setStatusC(String statusC) { this.statusC = statusC; }
        public void setFinalStatus(String finalStatus) { this.finalStatus = finalStatus; }
        public void setLastAttempt(String lastAttempt) { this.lastAttempt = lastAttempt; }
        public void setError(String error) { this.error = error; }
    }
}
