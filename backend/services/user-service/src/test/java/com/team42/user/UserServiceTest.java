package com.team42.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers() {
        User u1 = new User(1L, "System Admin", "admin@nexusmart.com", "avatar.jpg", "ROLE_ADMIN", "LOCAL");
        when(userRepository.findAll()).thenReturn(List.of(u1));

        List<User> users = userController.getAllUsers();
        assertEquals(1, users.size());
        assertEquals("ROLE_ADMIN", users.get(0).getRole());
    }

    @Test
    void testGetUserByEmail_Found() {
        User u1 = new User(2L, "Alex Johnson", "alex.johnson@gmail.com", "avatar.jpg", "ROLE_USER", "GOOGLE");
        when(userRepository.findByEmail("alex.johnson@gmail.com")).thenReturn(Optional.of(u1));

        ResponseEntity<User> response = userController.getUserByEmail("alex.johnson@gmail.com");
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals("ROLE_USER", response.getBody().getRole());
    }
}
