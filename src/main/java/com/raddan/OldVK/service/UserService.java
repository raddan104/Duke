package com.raddan.OldVK.service;

import com.raddan.OldVK.entity.User;
import com.raddan.OldVK.repository.UserRepository;
import com.raddan.OldVK.utils.AuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    private enum AllowedFields {
        userID, username, email, bio, dob
    }

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> getMyInfo(Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException(userDetails.getUsername() + " not found"));

            Map<String, String> userInfo = new ConcurrentHashMap<>();
            for (Field field : user.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (Arrays.stream(AllowedFields.values()).anyMatch(af -> af.name().equals(field.getName()))) {
                    Object value = field.get(user);
                    String valueAsString = (value != null) ? value.toString() : "null";
                    userInfo.put(field.getName(), valueAsString);
                }
            }
            return ResponseEntity.ok(userInfo);
        } catch (IllegalAccessException e) {
            logger.error("Error accessing field: {}", e.getMessage());
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Failed to fetch user info");
        }
    }

    public ResponseEntity<?> getUserInfo(Authentication authentication, String username) {
        try {
            UserDetails userDetails = AuthUtils.getUserDetails(authentication);
            User authorizedUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException(userDetails + " not found"));

            if (authorizedUser == null) {
                return ResponseEntity.status(UNAUTHORIZED).body("Please authorize before this request.");
            }

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException(username + " not found"));
            Map<String, String> userInfo = new ConcurrentHashMap<>();
            for (Field field : user.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (Arrays.stream(AllowedFields.values()).anyMatch(af -> af.name().equals(field.getName()))) {
                    Object value = field.get(user);
                    String valueAsString = (value != null) ? value.toString() : "null";
                    userInfo.put(field.getName(), valueAsString);
                }
            }
            return ResponseEntity.ok(userInfo);
        } catch (IllegalAccessException e) {
            logger.error("Error accessing user field: {}", e.getMessage());
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Failed to fetch user info");
        }
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

    public ResponseEntity<?> deleteUser(Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException(userDetails.getUsername() + " not found"));

            userRepository.delete(user);
            return ResponseEntity.ok(String.format("User '%s' deleted.", userDetails.getUsername()));
        } catch (RuntimeException e) {
            logger.error("Can't delete user: {}", e.getMessage());
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body("Can't delete user");
        }
    }
}
