package com.broker.chain;

import com.broker.model.BaseRetryJob;

public interface RetryHandler {
    void setNext(RetryHandler next);
    void handle(BaseRetryJob job);
}
