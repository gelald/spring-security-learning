package com.github.gelald.tinyss.config;

import com.github.gelald.tinyss.entity.User;
import com.github.gelald.tinyss.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if admin user already exists
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setPassword(passwordEncoder.encode("admin123")); // Default password
            admin.setEnabled(true);
            admin.setRoles(List.of("ADMIN")); // Assign ADMIN role
            
            userRepository.save(admin);
            System.out.println("Admin user created successfully with ADMIN role!");
        } else {
            System.out.println("Admin user already exists, skipping initialization.");
        }
    }
}