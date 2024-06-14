package com.raddan.OldVK.service;

import com.raddan.OldVK.entity.User;
import com.raddan.OldVK.enums.Roles;
import com.raddan.OldVK.exception.DeletionException;
import com.raddan.OldVK.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<String> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(User::getUsername).collect(Collectors.toList());
    }

    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    public User createUser(String username, String email, String password, String avatar, Roles role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setAvatar(avatar);
        user.setRole(role);
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        Optional<User> existingUser = userRepository.findById(user.getId());
        if (existingUser.isPresent()) {
            existingUser.get().setUsername(user.getUsername());
            existingUser.get().setEmail(user.getEmail());
            existingUser.get().setAvatar(user.getAvatar());
            existingUser.get().setRole(user.getRole());

            if (!user.getPassword().equals(existingUser.get().getPassword())) {
                existingUser.get().setPassword(passwordEncoder.encode(user.getPassword()));
            }

            return userRepository.save(existingUser.get());
        }
        throw new IllegalArgumentException("User with username: " + user.getUsername() + " not found!");
    }

    public void deleteUser(Long userId) {
        try {
            userRepository.deleteById(userId);
        } catch (RuntimeException e) {
            String message = "Failed to delete user with ID: " + userId;
            String cause = e.getMessage();
            throw new DeletionException(message + "\nCause: " + cause, e);
        }
    }
}
