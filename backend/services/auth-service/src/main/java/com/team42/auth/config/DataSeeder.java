package com.team42.auth.config;

import com.team42.auth.model.UserEntity;
import com.team42.auth.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedUsers(UserRepository repository, PasswordEncoder encoder) {
        return args -> {
            if (repository.count() == 0) {
                // Demo user for quick testing
                UserEntity demo = new UserEntity(
                    "Zayan Ahmed",
                    "zayan@cinemaseat.com",
                    encoder.encode("password123"),
                    "ROLE_USER"
                );
                demo.setPhone("01700000000");
                repository.save(demo);

                UserEntity admin = new UserEntity(
                    "Admin",
                    "admin@cinemaseat.com",
                    encoder.encode("admin123"),
                    "ROLE_ADMIN"
                );
                repository.save(admin);

                System.out.println("✅ Auth: Seeded demo users (zayan@cinemaseat.com / password123)");
            }
        };
    }
}
