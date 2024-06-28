package com.raddan.OldVK.service;

import com.raddan.OldVK.entity.User;
import com.raddan.OldVK.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> updateUser(UserDetails userDetails, Map<String, Object> updates) {
        try {
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException(userDetails.getUsername() + " not found"));

            for (Map.Entry<String, Object> entry : updates.entrySet()) {
                Field field = User.class.getDeclaredField(entry.getKey());
                field.setAccessible(true);
                field.set(user, entry.getValue());
            }

            user.setUpdatedAt(LocalDate.now());
            userRepository.save(user);
            return ResponseEntity.ok("User updated successfully!");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.error("{} tried to updated his info with illegal data", userDetails.getUsername());
            return ResponseEntity.status(403).body("Error updating user. Checkout your data!");
        }
    }
}
