package myuserservice;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class UserServiceSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceSpringApplication.class, args);
    }
}
