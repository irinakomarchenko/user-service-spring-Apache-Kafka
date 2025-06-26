package myuserservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import myuserservice.dto.UserDto;
import myuserservice.dto.UserEventDto;
import myuserservice.entity.OutboxEventType;
import myuserservice.mapper.UserMapper;
import myuserservice.entity.OutboxMessage;
import myuserservice.entity.User;
import myuserservice.repository.OutboxRepository;
import myuserservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor

public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;


    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toEntity(userDto);
        user.setId(null);
        User saved = userRepository.save(user);
        log.info("Пользователь добавлен: {}", saved);


        sendOutboxEvent(OutboxEventType.CREATE, saved.getEmail());

        return UserMapper.toDto(saved);
    }

    @Override
    public UserDto getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id " + id + " не найден"));
        log.info("Пользователь найден: {}", user);
        return UserMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        log.info("Найдено пользователей: {}", users.size());
        return users.stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id " + id + " не найден"));
        log.info("Обновление пользователя с id {}: {}", id, userDto);
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setAge(userDto.getAge());

        User updated = userRepository.save(user);
        log.info("Пользователь обновлён: {}", updated);
        return UserMapper.toDto(updated);    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id " + id + " не найден"));
        userRepository.deleteById(id);
        log.info("Пользователь с id {} удалён", id);

        sendOutboxEvent(OutboxEventType.DELETE, user.getEmail());
    }

    private void sendOutboxEvent(OutboxEventType eventType, String email) {
        try {
            UserEventDto event = UserEventDto.builder()
                    .operation(eventType.name())
                    .email(email)
                    .build();
            String payload = objectMapper.writeValueAsString(event);

            OutboxMessage outbox = OutboxMessage.builder()
                    .eventType(eventType)
                    .payload(payload)
                    .createdAt(LocalDateTime.now())
                    .processed(false)
                    .build();

            outboxRepository.save(outbox);
            log.info("Outbox-событие создано: {}", outbox);
        } catch (Exception e) {
            log.error("Ошибка сериализации Outbox-события: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка сериализации outbox", e);
        }

    }
}
