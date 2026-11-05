package com.example.claims.orchestration.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordHashServiceTest {

    private final PasswordHashService passwordHashService = new PasswordHashService();

    @Test
    void verifiesSeededDemoPasswordHash() {
        assertTrue(passwordHashService.matches(
                "ClaimantDemo!26",
                "pbkdf2-sha256$120000$0hVYsi7Wy4fsGJyBNoeRYg==$iaz8h8JdDnrIZriiHxjiDj6AYARTkbqR3xsFbK50TAY="));
    }

    @Test
    void rejectsIncorrectPassword() {
        assertFalse(passwordHashService.matches(
                "not-the-demo-password",
                "pbkdf2-sha256$120000$0hVYsi7Wy4fsGJyBNoeRYg==$iaz8h8JdDnrIZriiHxjiDj6AYARTkbqR3xsFbK50TAY="));
    }
}
