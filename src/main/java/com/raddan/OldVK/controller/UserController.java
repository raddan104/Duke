package com.raddan.OldVK.controller;

import com.raddan.OldVK.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/profile")
    public ResponseEntity<?> getMyProfile(Authentication authentication) {
        return userService.getMyInfo(authentication);
    }

    @GetMapping(path = "/{username}")
    public ResponseEntity<?> getUserProfile(Authentication authentication,
                                            @PathVariable String username) {
        return userService.getUserInfo(authentication, username);
    }

    @PutMapping(path = "/profile/edit")
    public ResponseEntity<?> updateUser(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestBody Map<String, Object> updates) {
        return userService.updateUser(userDetails, updates);
    }

    @DeleteMapping(path = "/profile/delete")
    public ResponseEntity<?> deleteUser(Authentication authentication) {
        return userService.deleteUser(authentication);
    }
}
