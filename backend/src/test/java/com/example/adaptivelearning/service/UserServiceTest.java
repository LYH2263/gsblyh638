package com.example.adaptivelearning.service;

import com.example.adaptivelearning.exception.BusinessException;
import com.example.adaptivelearning.exception.UnauthorizedException;
import com.example.adaptivelearning.model.User;
import com.example.adaptivelearning.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setEmail("test@example.com");
    }

    @Test
    void testRegister_Success() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User registered = userService.register(testUser);

        assertNotNull(registered);
        assertEquals("testuser", registered.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegister_UserExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(BusinessException.class, () -> userService.register(testUser));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLogin_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        User loggedIn = userService.login("testuser", "password123");

        assertNotNull(loggedIn);
        assertEquals("testuser", loggedIn.getUsername());
    }

    @Test
    void testLogin_InvalidPassword() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        assertThrows(UnauthorizedException.class, () -> userService.login("testuser", "wrongpassword"));
    }

    @Test
    void testLogin_UserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> userService.login("unknown", "password"));
    }
}
