package com.team42.user;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {


    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner initUsers(UserRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.saveAll(List.of(
                    new User(1L, "System Administrator", "admin@nexusmart.com", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80", "ROLE_ADMIN", "LOCAL"),
                    new User(2L, "Operations Manager", "manager@nexusmart.com", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80", "ROLE_MANAGER", "LOCAL"),
                    new User(3L, "Alex Johnson", "alex.johnson@gmail.com", "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150&auto=format&fit=crop&q=80", "ROLE_USER", "GOOGLE")
                ));
                System.out.println("✅ Populated initial users with ROLE_ADMIN, ROLE_MANAGER, and ROLE_USER.");
            }
        };
    }
}
