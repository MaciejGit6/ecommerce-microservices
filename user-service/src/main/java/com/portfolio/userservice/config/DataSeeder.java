package com.portfolio.userservice.config;

import com.portfolio.userservice.entity.User;
import com.portfolio.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already has data — skipping seed.");
            return;
        }

        log.info("Seeding sample users...");

        userRepository.save(User.builder()
                .username("alice")
                .email("alice@example.com")
                .password("password123")  
                .build());

        userRepository.save(User.builder()
                .username("bob")
                .email("bob@example.com")
                .password("password456")
                .build());

        log.info("Seeded {} users.", userRepository.count());
    }
}