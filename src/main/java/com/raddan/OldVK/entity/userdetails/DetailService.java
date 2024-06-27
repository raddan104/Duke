package com.raddan.OldVK.entity.userdetails;

import com.raddan.OldVK.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service(value = "detailService")
public class DetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public DetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String principal) throws UsernameNotFoundException {
        return this
                .userRepository
                .findByPrincipal(principal)
                .map(UserDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException(principal + " not found"));
    }
}
