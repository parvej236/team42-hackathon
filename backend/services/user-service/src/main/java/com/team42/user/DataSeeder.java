package com.team42.user;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedUsers(UserRepository repository) {
        return args -> {
            // Re-seed default users if missing
            if (!repository.existsByEmail("zayan@cinemaseat.com")) {
                repository.save(new User(
                    null,
                    "Zayan Ahmed",
                    "zayan@cinemaseat.com",
                    "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150&auto=format&fit=crop&q=80",
                    "ROLE_USER",
                    "LOCAL"
                ));
            }

            if (!repository.existsByEmail("admin@cinemaseat.com")) {
                repository.save(new User(
                    null,
                    "CinemaSeat Admin",
                    "admin@cinemaseat.com",
                    "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80",
                    "ROLE_ADMIN",
                    "LOCAL"
                ));
            }

            if (!repository.existsByEmail("admin@nexusmart.com")) {
                repository.save(new User(
                    null,
                    "System Administrator",
                    "admin@nexusmart.com",
                    "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80",
                    "ROLE_ADMIN",
                    "LOCAL"
                ));
            }

            if (!repository.existsByEmail("manager@nexusmart.com")) {
                repository.save(new User(
                    null,
                    "Operations Manager",
                    "manager@nexusmart.com",
                    "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80",
                    "ROLE_MANAGER",
                    "LOCAL"
                ));
            }

            if (!repository.existsByEmail("alex.johnson@gmail.com")) {
                repository.save(new User(
                    null,
                    "Alex Johnson",
                    "alex.johnson@gmail.com",
                    "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150&auto=format&fit=crop&q=80",
                    "ROLE_USER",
                    "GOOGLE"
                ));
            }

            System.out.println("✅ User-Service: Seeded all requested user profiles into user_schema.");
        };
    }
}
