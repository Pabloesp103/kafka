package com.broker.repository;

import com.broker.model.PaymentRetryJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentRetryJobRepository extends JpaRepository<PaymentRetryJob, Long> {
    List<PaymentRetryJob> findByFinalStatus(String finalStatus);
}
