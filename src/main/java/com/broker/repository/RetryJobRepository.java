package com.broker.repository;

import com.broker.model.RetryJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RetryJobRepository extends JpaRepository<RetryJob, Long> {
    List<RetryJob> findByFinalStatus(String finalStatus);
}
