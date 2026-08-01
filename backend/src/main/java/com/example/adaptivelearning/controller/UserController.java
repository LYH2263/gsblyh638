package com.example.adaptivelearning.controller;

import com.example.adaptivelearning.dto.LoginRequest;
import com.example.adaptivelearning.dto.RegisterRequest;
import com.example.adaptivelearning.model.User;
import com.example.adaptivelearning.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173") // Allow Vue dev server
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Received registration request for username: {}", request.getUsername());
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setRole("USER"); // Default role
        user.setAvatar("https://api.dicebear.com/7.x/avataaars/svg?seed=" + request.getUsername());
        
        user.setNickname(request.getNickname() != null ? request.getNickname() : "新用户");
        user.setBio(request.getBio());
        user.setLearningGoals(request.getLearningGoals());
        
        User registeredUser = userService.register(user);
        log.info("User registered successfully: {}", registeredUser.getUsername());
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        log.info("Received login request for username: {}", request.getUsername());
        User user = userService.login(request.getUsername(), request.getPassword());
        log.info("User logged in successfully: {}", user.getUsername());
        return ResponseEntity.ok(user);
    }
}
