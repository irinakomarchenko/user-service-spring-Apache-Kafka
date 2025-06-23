package notificationservice.kafka;


import lombok.RequiredArgsConstructor;
import notificationservice.dto.UserEventNotificDto;
import notificationservice.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(UserEventConsumer.class);

    private final EmailService emailService;

    @KafkaListener(topics = "user-events", groupId = "notification-service")
    public void listen(UserEventNotificDto event) {
        log.info("Получено событие из Kafka: operation={}, email={}", event.getOperation(), event.getEmail());
        try {
            if ("CREATE".equals(event.getOperation())) {
                emailService.send(event.getEmail(), "Ваш аккаунт создан", "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.");
            } else if ("DELETE".equals(event.getOperation())) {
                emailService.send(event.getEmail(), "Ваш аккаунт удалён", "Здравствуйте! Ваш аккаунт был удалён.");
            } else {
                log.warn("Неизвестная операция: {}", event.getOperation());
            }
        } catch (Exception e) {
            log.error("Ошибка обработки события из Kafka: {}", e.getMessage(), e);
        }
    }
}
