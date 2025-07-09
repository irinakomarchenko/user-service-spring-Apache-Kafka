package circuitbreaker.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/cb")
public class CircuitBreakerController {

    private final RestTemplate restTemplate;

    @Value("${user.service.url:http://user-service:8080/api/users}")
    private String userServiceUrl;

    public CircuitBreakerController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/users")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "usersFallback")
    public ResponseEntity<String> getUsers() {
        String response = restTemplate.getForObject(userServiceUrl, String.class);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<String> usersFallback(Exception e) {
        return ResponseEntity.ok("Fallback response: User service is currently unavailable.");
    }

}
