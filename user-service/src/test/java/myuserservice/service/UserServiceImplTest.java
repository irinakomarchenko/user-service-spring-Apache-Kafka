package myuserservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import myuserservice.dto.UserDto;
import myuserservice.dto.UserEventDto;
import myuserservice.entity.OutboxMessage;
import myuserservice.entity.User;
import myuserservice.repository.OutboxRepository;
import myuserservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private OutboxRepository outboxRepository;
    @Mock private ObjectMapper objectMapper;

    @InjectMocks private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .id(1L)
                .name("Alice")
                .email("alice@example.com")
                .age(25)
                .build();
        userDto = UserDto.builder()
                .id(1L)
                .name("Alice")
                .email("alice@example.com")
                .age(25)
                .build();
    }

    @Test
    void createUser_shouldReturnCreatedUser_andSaveOutbox() throws Exception {
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(objectMapper.writeValueAsString(any(UserEventDto.class))).thenReturn("{json}");

        UserDto result = userService.createUser(userDto);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("alice@example.com");
        verify(userRepository).save(any(User.class));
        verify(outboxRepository).save(any(OutboxMessage.class));
    }

    @Test
    void getUser_shouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.getUser(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(userRepository).findById(1L);
    }

    @Test
    void getUser_shouldThrowIfNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUser(1L));
    }

    @Test
    void getAllUsers_shouldReturnList() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<UserDto> users = userService.getAllUsers();
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void updateUser_shouldUpdateSuccessfully() {
        User updated = User.builder()
                .id(1L)
                .name("Updated")
                .email("alice@example.com")
                .age(30)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updated);

        UserDto result = userService.updateUser(1L, userDto);

        assertThat(result.getName()).isEqualTo("Updated");
    }

    @Test
    void deleteUser_shouldDelete_andSaveOutbox() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(1L);
        when(objectMapper.writeValueAsString(any(UserEventDto.class))).thenReturn("{json}");

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
        verify(outboxRepository).save(any(OutboxMessage.class));
    }
}