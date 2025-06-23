package myuserservice.kafka;

import lombok.RequiredArgsConstructor;
import myuserservice.entity.OutboxMessage;
import myuserservice.repository.OutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class OutboxPublisher {
    private static final  Logger log = LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 50000)
    public void publishOutboxMessages() {
        List<OutboxMessage> messages = outboxRepository.findByProcessedFalse();
        for (OutboxMessage msg : messages) {
            try {
                kafkaTemplate.send("user-events", msg.getPayload());
                msg.setProcessed(true);
                outboxRepository.save(msg);
                log.info("Outbox сообщение {} отправлено в Kafka", msg.getId());
            } catch (Exception e) {
                log.error("Ошибка отправки outbox сообщения {} в Kafka: {}", msg.getId(), e.getMessage());
            }
        }

    }
}
