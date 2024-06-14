package com.raddan.OldVK.service;

import com.raddan.OldVK.entity.User;
import com.raddan.OldVK.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
}
