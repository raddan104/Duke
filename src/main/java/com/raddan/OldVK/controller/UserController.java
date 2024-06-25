package com.raddan.OldVK.controller;

import com.raddan.OldVK.entity.User;
import com.raddan.OldVK.service.JwtService;
import com.raddan.OldVK.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secured!";
    }

    @GetMapping("/user/profile")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String userProfile() {
        return userService.getUserProfile();
    }

    @GetMapping("/admin/profile")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String adminProfile() {
        return userService.getUserProfile();
    }

    @GetMapping("/users")
    public List<String> listUsers() {
        return userService.getListOfUsers();
    }

    @PutMapping("/edit")
    public ResponseEntity<?> updateUser(@RequestBody Map<String, Object> updateData) {
        String result = userService.updateUser(updateData);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser() {
        String result = userService.deleteUser();
        return ResponseEntity.ok(result);
    }

}