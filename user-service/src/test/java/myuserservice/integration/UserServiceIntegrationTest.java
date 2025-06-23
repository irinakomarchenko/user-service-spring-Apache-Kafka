package myuserservice.integration;

import myuserservice.dto.UserDto;
import myuserservice.entity.User;
import myuserservice.repository.OutboxRepository;
import myuserservice.repository.UserRepository;
import myuserservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;



@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class UserServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private OutboxRepository outboxRepository;

    @BeforeEach
    void clean() {
        outboxRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testSaveUser() {
        var user = User.builder()
                .name("Zoe")
                .email("zoem@example.com")
                .age(38)
                .build();

        var saved = userRepository.save(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("zoem@example.com");
    }
    @Test
    void testUserCreate_savesOutboxMessage() {
        var dto = UserDto.builder().name("Zoe").email("zoem@example.com").age(38).build();
        userService.createUser(dto);
        var messages = outboxRepository.findAll();
        assertThat(messages).anyMatch(m -> m.getEventType().equals("CREATE"));
    }


}