package testcontainers;

import org.testcontainers.containers.GenericContainer;

public class MailHogContainer extends GenericContainer<MailHogContainer> {
    private static final int SMTP_PORT = 1025;
    private static final int HTTP_PORT = 8025;

    public MailHogContainer() {
        super("mailhog/mailhog:v1.0.1");
        withExposedPorts(SMTP_PORT, HTTP_PORT);
    }

    public Integer getSmtpPort() {
        return getMappedPort(SMTP_PORT);
    }

    public Integer getHttpPort() {
        return getMappedPort(HTTP_PORT);
    }
}
