package com.broker.repository;

import com.broker.model.OrderRetryJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRetryJobRepository extends JpaRepository<OrderRetryJob, Long> {
    List<OrderRetryJob> findByFinalStatus(String finalStatus);
}
