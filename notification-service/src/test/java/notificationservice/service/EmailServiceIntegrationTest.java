package notificationservice.service;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.InternetAddress;
import testcontainers.MailHogContainer;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
public class EmailServiceIntegrationTest {
    @Container
    static MailHogContainer mailHog = new MailHogContainer();

    @DynamicPropertySource
    static void mailhogProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mail.host", mailHog::getHost);
        registry.add("spring.mail.port", mailHog::getSmtpPort);
    }

    @Autowired
    private JavaMailSender mailSender;

    @Test
    public void testSendEmailAndCheckMailhog() throws Exception {

        MimeMessage message = mailSender.createMimeMessage();
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress("test@user.com"));
        message.setFrom(new InternetAddress("noreply@ynotification.com"));
        message.setSubject("Test subject");
        message.setText("Hello, this is a test email!");
        mailSender.send(message);


        Thread.sleep(500);

        URL url = new URL("http://" + mailHog.getHost() + ":" + mailHog.getHttpPort() + "/api/v2/messages");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (Scanner scanner = new Scanner(connection.getInputStream(), StandardCharsets.UTF_8)) {
            String responseBody = scanner.useDelimiter("\\A").next();
            assertThat(responseBody).contains("Hello, this is a test email!");
            assertThat(responseBody).contains("test@user.com");
        }
    }
}
