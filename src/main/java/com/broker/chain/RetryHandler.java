package com.broker.chain;

import com.broker.model.RetryJob;

public interface RetryHandler {
    void setNext(RetryHandler next);
    void handle(RetryJob job);
}
