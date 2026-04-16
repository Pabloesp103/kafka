package com.broker.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_retry_jobs")
public class OrderRetryJob extends BaseRetryJob {
    public OrderRetryJob() {}
}
