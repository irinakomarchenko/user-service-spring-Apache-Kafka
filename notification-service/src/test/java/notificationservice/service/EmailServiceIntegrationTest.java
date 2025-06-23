package notificationservice.service;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.InternetAddress;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class EmailServiceIntegrationTest {

    @Autowired
    private JavaMailSender mailSender;

    @Test
    public void testSendEmail() throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress("test@user.com"));
        message.setFrom(new InternetAddress("noreply@yourapp.com"));
        message.setSubject("Test subject");
        message.setText("Hello, this is a test email!");

        mailSender.send(message);

        assertThat(message.getAllRecipients()).isNotEmpty();
    }
}
