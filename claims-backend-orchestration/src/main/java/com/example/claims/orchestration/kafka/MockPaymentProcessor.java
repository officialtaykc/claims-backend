package com.example.claims.orchestration.kafka;

import com.example.claims.common.dto.PaymentProcessedEvent;
import com.example.claims.common.dto.PaymentRequestedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class MockPaymentProcessor {

    private static final Logger log = LoggerFactory.getLogger(MockPaymentProcessor.class);

    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final String paymentResponseTopic;
    private final long mockDelayMillis;

    public MockPaymentProcessor(KafkaTemplate<Object, Object> kafkaTemplate,
                                @Value("${claims.kafka.payment-response-topic}") String paymentResponseTopic,
                                @Value("${claims.kafka.mock-payment-delay-ms}") long mockDelayMillis) {
        this.kafkaTemplate = kafkaTemplate;
        this.paymentResponseTopic = paymentResponseTopic;
        this.mockDelayMillis = mockDelayMillis;
    }

    @KafkaListener(topics = "${claims.kafka.payment-request-topic}", groupId = "claims-mock-payment")
    public void processPaymentRequest(PaymentRequestedEvent request) {
        log.info("Mock payment processor received claimReference={} eventId={}",
                request.getClaimReference(), request.getEventId());
        try {
            Thread.sleep(mockDelayMillis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while simulating payment processing", exception);
        }

        PaymentProcessedEvent response = new PaymentProcessedEvent();
        response.setEventId(request.getEventId());
        response.setClaimReference(request.getClaimReference());
        response.setPaymentReference("MOCK-" + request.getEventId());
        try {
            kafkaTemplate.send(paymentResponseTopic, response.getClaimReference(), response)
                    .get(10, TimeUnit.SECONDS);
            log.info("Mock payment completed claimReference={} paymentReference={}",
                    response.getClaimReference(), response.getPaymentReference());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while publishing mock payment response", exception);
        } catch (ExecutionException | java.util.concurrent.TimeoutException exception) {
            throw new IllegalStateException("Failed to publish mock payment response for "
                    + response.getClaimReference(), exception);
        }
    }
}
