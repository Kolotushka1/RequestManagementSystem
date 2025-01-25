package org.vaadin.example;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.vaadin.example.domain.AppUser;
import org.vaadin.example.domain.Role;
import org.vaadin.example.service.UserService;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@PWA(name = "Project Base for Vaadin with Spring", shortName = "Project Base")
@Theme("my-theme")
@Push
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner init(UserService userService) {
        return args -> {
            userService.findByName("client1").orElseGet(() ->
                    userService.createUser("client1", "pass", Role.CLIENT)
            );
            userService.findByName("operator1").orElseGet(() ->
                    userService.createUser("operator1", "pass", Role.OPERATOR)
            );
        };
    }
}
