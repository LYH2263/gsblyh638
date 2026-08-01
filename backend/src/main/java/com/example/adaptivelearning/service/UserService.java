package com.example.adaptivelearning.service;

import com.example.adaptivelearning.exception.BusinessException;
import com.example.adaptivelearning.exception.UnauthorizedException;
import com.example.adaptivelearning.model.User;
import com.example.adaptivelearning.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
             throw new BusinessException("用户名已存在");
        }
        return userRepository.save(user);
    }

    public User login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(password)) {
                return user;
            }
        }
        throw new UnauthorizedException("用户名或密码错误");
    }
}
