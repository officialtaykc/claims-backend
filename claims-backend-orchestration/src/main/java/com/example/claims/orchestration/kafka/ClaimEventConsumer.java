package com.example.claims.orchestration.kafka;

import com.example.claims.common.dto.ClaimSubmittedEvent;
import com.example.claims.orchestration.service.ClaimPersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ClaimEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ClaimEventConsumer.class);

    private final ClaimPersistenceService claimPersistenceService;

    public ClaimEventConsumer(ClaimPersistenceService claimPersistenceService) {
        this.claimPersistenceService = claimPersistenceService;
    }

    @KafkaListener(topics = "${claims.kafka.topic}", groupId = "claims-orchestration")
    public void consume(ClaimSubmittedEvent event) {
        log.info("Received claim event claimReference={} eventId={}",
                event.getClaimReference(), event.getEventId());
        claimPersistenceService.persistClaim(event);
    }
}
