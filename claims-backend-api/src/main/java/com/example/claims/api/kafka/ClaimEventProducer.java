package com.example.claims.api.kafka;

import com.example.claims.common.dto.ClaimSubmittedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class ClaimEventProducer {

    private static final Logger log = LoggerFactory.getLogger(ClaimEventProducer.class);

    private final KafkaTemplate<String, ClaimSubmittedEvent> kafkaTemplate;
    private final String topic;

    public ClaimEventProducer(KafkaTemplate<String, ClaimSubmittedEvent> kafkaTemplate,
                              @Value("${claims.kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(ClaimSubmittedEvent event) {
        try {
            SendResult<String, ClaimSubmittedEvent> result = kafkaTemplate
                    .send(topic, event.getClaimReference(), event)
                    .get(10, TimeUnit.SECONDS);
            log.info("Published claim event claimReference={} eventId={} partition={} offset={}",
                    event.getClaimReference(), event.getEventId(),
                    result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaPublishException("Interrupted while publishing claim event", e);
        } catch (ExecutionException | TimeoutException e) {
            throw new KafkaPublishException("Failed to publish claim event to Kafka", e);
        }
    }
}
