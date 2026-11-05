package com.example.claims.orchestration.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${claims.kafka.topic}")
    private String claimsTopic;

    @Value("${claims.kafka.payment-request-topic}")
    private String paymentRequestTopic;

    @Value("${claims.kafka.payment-response-topic}")
    private String paymentResponseTopic;

    @Bean
    public NewTopic claimsSubmittedTopic() {
        return TopicBuilder.name(claimsTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentRequestedTopic() {
        return TopicBuilder.name(paymentRequestTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentProcessedTopic() {
        return TopicBuilder.name(paymentResponseTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
