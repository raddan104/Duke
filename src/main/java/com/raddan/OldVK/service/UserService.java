package com.raddan.OldVK.service;

import com.raddan.OldVK.entity.User;
import com.raddan.OldVK.exception.custom.IllegalUserDetailsException;
import com.raddan.OldVK.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

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

    public String getUserProfile() {
        User authorizedUser = userRepository.findByUsername(getUsernameFromJwt())
                .orElseThrow(() -> new JwtException("Your token is expired or illegal!"));
        return "Profile: " + authorizedUser.getUsername() +
                "\nRole: " + authorizedUser.getRoles() +
                "\nFriends: " + authorizedUser.getFriends().size();
    }

    public String createUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalUserDetailsException("User with this username already exists!");
        } else if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalUserDetailsException("User with this e-mail already exists!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        String role = user.getRoles();
        if (role == null) {
            user.setRoles("ROLE_USER");
        }
        userRepository.save(user);
        return "User successfully signed up!";
    }

    public String updateUser(Map<String, Object> updateData) {
        Optional<User> userOptional = userRepository.findById(getIdFromJwt());

        return userOptional.map(user -> {
            try {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                for (Map.Entry<String, Object> entry : updateData.entrySet()) {
                    String fieldName = entry.getKey();
                    Object fieldValue = entry.getValue();

                    Field field = User.class.getDeclaredField(fieldName);
                    field.setAccessible(true);

                    if (fieldName.equals("password")) {
                        String encodedPassword = passwordEncoder.encode((String) fieldValue);
                        field.set(user, encodedPassword);
                    } else if (field.getType() == LocalDate.class && fieldValue instanceof String) {
                        LocalDate localDate = LocalDate.parse((String) fieldValue, dateFormatter);
                        field.set(user, localDate);
                    } else {
                        field.set(user, fieldValue);
                    }
                }

                userRepository.save(user);
                log.info("User {} updated successfully!", user.getUsername());
                return String.format("'%s' updated successfully", userOptional.get().getUsername());
            } catch (Exception e) {
                log.error(e.getMessage());
                return "OUCH! Some of your field is broken!";
            }
        }).orElse("User not found!");
    }

    public List<String> getListOfUsers() {
        return userRepository.findAll().stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
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

    public String getUsernameFromJwt() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    public Long getIdFromJwt() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            Optional<User> optionalUser = userRepository.findByUsername(username);
            if (optionalUser.isPresent()) {
                return optionalUser.get().getId();
            } else {
                Logger logger = LoggerFactory.getLogger(getClass());
                logger.error("User not found for username: " + username);

                throw new IllegalStateException("User not found for username: " + username);
            }
        } else {
            Logger logger = LoggerFactory.getLogger(getClass());
            logger.error("Principal is not an instance of UserDetails. Principal: " + principal);

            throw new IllegalStateException("Unexpected principal type: " + principal.getClass().getName());
        }
    }

}
