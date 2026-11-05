package com.example.claims.orchestration.kafka;

import com.example.claims.common.dto.PaymentRequestedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class PaymentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventPublisher.class);

    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final String paymentRequestTopic;

    public PaymentEventPublisher(KafkaTemplate<Object, Object> kafkaTemplate,
                                 @Value("${claims.kafka.payment-request-topic}") String paymentRequestTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.paymentRequestTopic = paymentRequestTopic;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishPaymentRequest(PaymentRequestedEvent event) {
        try {
            kafkaTemplate.send(paymentRequestTopic, event.getClaimReference(), event)
                    .get(10, TimeUnit.SECONDS);
            log.info("Published payment request claimReference={} eventId={} amount={} {}",
                    event.getClaimReference(), event.getEventId(), event.getAmount(), event.getCurrency());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while publishing payment request", exception);
        } catch (ExecutionException | TimeoutException exception) {
            throw new IllegalStateException("Failed to publish payment request for "
                    + event.getClaimReference(), exception);
        }
    }
}
