package com.seolstudy.seolstudy_backend.auth;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {

    @Test
    void generateBCryptHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String rawPassword = "1234";
        String encodedPassword = encoder.encode(rawPassword);

        System.out.println("===========================================");
        System.out.println("Raw password: " + rawPassword);
        System.out.println("BCrypt hash: " + encodedPassword);
        System.out.println("===========================================");

        // Verify it matches
        boolean matches = encoder.matches(rawPassword, encodedPassword);
        System.out.println("Verification: " + matches);
    }
}
