package com.portfolio.userservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StartupListener {

    @Value("${server.port:8081}")
    private int port;

    @Value("${spring.application.name:user-service}")
    private String serviceName;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        String banner = """
                
                ╔══════════════════════════════════════════════════╗
                ║          USER SERVICE — READY                    ║
                ╠══════════════════════════════════════════════════╣
                ║  Base URL : http://localhost:%d/api/users         ║
                ║  Health   : http://localhost:%d/actuator/health   ║
                ║  H2 (dev) : http://localhost:%d/h2-console        ║
                ╠══════════════════════════════════════════════════╣
                ║  Endpoints:                                      ║
                ║    GET    /api/users          — list all          ║
                ║    GET    /api/users/{id}     — find by ID        ║
                ║    POST   /api/users          — create            ║
                ║    PUT    /api/users/{id}     — update            ║
                ║    DELETE /api/users/{id}     — delete            ║
                ╚══════════════════════════════════════════════════╝
                """.formatted(port, port, port);

        log.info(banner);
    }
}