package notificationservice.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.InternetAddress;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class EmailServiceIntegrationTest {
    @Autowired
    private JavaMailSender mailSender;

    @Test
    public void testSendEmailAndCheckMailhog() throws Exception {

        MimeMessage message = mailSender.createMimeMessage();
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress("test@user.com"));
        message.setFrom(new InternetAddress("noreply@yourapp.com"));
        message.setSubject("Test subject");
        message.setText("Hello, this is a test email!");
        mailSender.send(message);


        Thread.sleep(500);

        URL url = new URL("http://localhost:8025/api/v2/messages");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (Scanner scanner = new Scanner(connection.getInputStream(), StandardCharsets.UTF_8)) {
            String responseBody = scanner.useDelimiter("\\A").next();
            assertThat(responseBody).contains("Hello, this is a test email!");
            assertThat(responseBody).contains("test@user.com");
        }
    }
}
