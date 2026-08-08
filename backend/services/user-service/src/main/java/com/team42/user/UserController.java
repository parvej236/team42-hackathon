package com.team42.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        return userRepository.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return userRepository.findByEmail(user.getEmail())
                .map(existing -> {
                    if (user.getName() != null) existing.setName(user.getName());
                    if (user.getRole() != null) existing.setRole(user.getRole());
                    if (user.getAvatar() != null) existing.setAvatar(user.getAvatar());
                    if (user.getProvider() != null) existing.setProvider(user.getProvider());
                    return ResponseEntity.ok(userRepository.save(existing));
                })
                .orElseGet(() -> {
                    user.setId(null); // Ensure auto-generated ID is used to prevent overwriting existing IDs
                    return ResponseEntity.ok(userRepository.save(user));
                });
    }
}
