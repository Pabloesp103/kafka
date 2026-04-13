package com.broker.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic productRetryTopic() {
        return TopicBuilder.name("product_retry_jobs").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic orderRetryTopic() {
        return TopicBuilder.name("order_retry_jobs").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic paymentRetryTopic() {
        return TopicBuilder.name("payments_retry_jobs").partitions(1).replicas(1).build();
    }
}
