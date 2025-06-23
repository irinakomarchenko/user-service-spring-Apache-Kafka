package notificationservice.controller;


import lombok.RequiredArgsConstructor;
import notificationservice.dto.UserEventNotificDto;
import notificationservice.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;
    private static final Logger log = LoggerFactory.getLogger(EmailController.class);

    @PostMapping("/send")
    public void sendEmail(@RequestBody UserEventNotificDto event) {
        log.info("Запрос на отправку письма через API: operation={}, email={}", event.getOperation(), event.getEmail());
        if ("CREATE".equals(event.getOperation())) {
            emailService.send(event.getEmail(), "Ваш аккаунт создан", "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.");
        } else if ("DELETE".equals(event.getOperation())) {
            emailService.send(event.getEmail(), "Ваш аккаунт удалён", "Здравствуйте! Ваш аккаунт был удалён.");
        } else {
            log.warn("Неизвестная операция: {}", event.getOperation());
        }

    }



}