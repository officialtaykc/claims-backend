package com.example.claims.orchestration.kafka;

import com.example.claims.common.dto.PaymentProcessedEvent;
import com.example.claims.orchestration.service.ClaimService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentProcessedConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentProcessedConsumer.class);

    private final ClaimService claimService;

    public PaymentProcessedConsumer(ClaimService claimService) {
        this.claimService = claimService;
    }

    @KafkaListener(topics = "${claims.kafka.payment-response-topic}", groupId = "claims-payment-status")
    public void consume(PaymentProcessedEvent event) {
        log.info("Received payment completion claimReference={} paymentReference={}",
                event.getClaimReference(), event.getPaymentReference());
        claimService.markPaymentSettled(event);
    }
}
