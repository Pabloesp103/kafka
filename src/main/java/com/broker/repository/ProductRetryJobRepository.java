package com.broker.repository;

import com.broker.model.ProductRetryJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRetryJobRepository extends JpaRepository<ProductRetryJob, Long> {
    List<ProductRetryJob> findByFinalStatus(String finalStatus);
}
