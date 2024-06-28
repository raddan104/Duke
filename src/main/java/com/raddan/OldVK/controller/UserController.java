package com.raddan.OldVK.controller;

import com.raddan.OldVK.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/profile")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        return userService.getUserInfo(authentication);
    }

    @PutMapping("/edit")
    public ResponseEntity<?> updateUser(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestBody Map<String, Object> updates) {
        return userService.updateUser(userDetails, updates);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser(Authentication authentication) {
        return userService.deleteUser(authentication);
    }
}
