package com.raddan.OldVK.service;

import com.raddan.OldVK.entity.User;
import com.raddan.OldVK.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userDetail = userRepository.findByUsername(username);
        return userDetail.map(UserDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User '%s' not found!", username)));
    }

    public String createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        String role = user.getRoles();
        if (role == null) {
            user.setRoles("ROLE_USER");
        }
        userRepository.save(user);
        return "User successfully signed up!";
    }

    public String updateUser(Map<String, Object> updateData) {
        String username = getUsernameFromJwt();
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            LinkedHashMap<String, Object> updateFields = new LinkedHashMap<>(updateData);

            for (Map.Entry<String, Object> entry : updateFields.entrySet()) {
                String fieldName = entry.getKey();
                Object fieldValue = entry.getValue();

                switch (fieldName) {
                    case "username":
                        user.setUsername((String) fieldValue);
                        break;
                    case "email":
                        user.setEmail((String) fieldValue);
                        break;
                    case "password":
                        user.setPassword(passwordEncoder.encode((String) fieldValue));
                        break;
                    case "avatar":
                        user.setAvatar((String) fieldValue);
                        break;
                    case "roles":
                        user.setRoles((String) fieldValue);
                        break;
                }
            }

            userRepository.save(user);
            return "User updated successfully!";
        } else {
            return "User not found!";
        }
    }

    public String deleteUser() {
        String username = getUsernameFromJwt();
        Optional<User> userForRemoval = userRepository.findByUsername(username);
        if (userForRemoval.isPresent()) {
            userRepository.deleteById(userForRemoval.get().getId());
            return String.format("User '%s' deleted!", username);
        } else {
            return "User not found!";
        }
    }

    private String getUsernameFromJwt() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }
}
