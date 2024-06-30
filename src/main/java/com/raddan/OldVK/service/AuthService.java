package com.raddan.OldVK.service;

import com.raddan.OldVK.dto.AuthDTO;
import com.raddan.OldVK.entity.Profile;
import com.raddan.OldVK.entity.Role;
import com.raddan.OldVK.entity.User;
import com.raddan.OldVK.enums.RoleEnum;
import com.raddan.OldVK.repository.ProfileRepository;
import com.raddan.OldVK.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Setter
public class AuthService {

    @Value(value = "${custom.max.session}")
    private int maxSession;

    @Value(value = "${admin.username}")
    private String adminUsername;

    private final UserRepository userRepository;

    private final ProfileRepository profileRepository;

    private final PasswordEncoder passwordEncoder;

    private final SecurityContextRepository securityContextRepository;

    private final SecurityContextHolderStrategy securityContextHolderStrategy;

    private final AuthenticationManager authManager;

    private final RedisIndexedSessionRepository redisIndexedSessionRepository;

    private final SessionRegistry sessionRegistry;

    public AuthService(
            UserRepository userRepository, ProfileRepository profileRepository,
            PasswordEncoder passwordEncoder,
            SecurityContextRepository securityContextRepository,
            AuthenticationManager authManager,
            RedisIndexedSessionRepository redisIndexedSessionRepository,
            SessionRegistry sessionRegistry
    ) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityContextRepository = securityContextRepository;
        this.securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
        this.authManager = authManager;
        this.redisIndexedSessionRepository = redisIndexedSessionRepository;
        this.sessionRegistry = sessionRegistry;
    }

    public String register(AuthDTO dto) {
        String username = dto.username().trim();

        Optional<User> exists = userRepository
                .findByPrincipal(username);

        if (exists.isPresent()) {
            throw new IllegalStateException(username + " exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        user.addRole(new Role(RoleEnum.USER));
        Profile profile = ProfileService.createProfile(user);
        profileRepository.save(profile);

        if (adminUsername.equals(username)) {
            user.addRole(new Role(RoleEnum.ADMIN));
        }

        userRepository.save(user);
        return "Register!";
    }

    public String login(AuthDTO dto, HttpServletRequest request, HttpServletResponse response) {
        // Validating user credentials...
        Authentication authentication = authManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(
                dto.username().trim(), dto.password()));

        // Validating session constraint is not exceeded
        validateMaxSession(authentication);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        this.securityContextHolderStrategy.setContext(context);
        this.securityContextRepository.saveContext(context, request, response);

        return "Logged In!";
    }

    private void validateMaxSession(Authentication authentication) {
        if (maxSession <= 0) {
            return;
        }

        UserDetails principal = (UserDetails) authentication.getPrincipal();
        List<SessionInformation> sessions = this.sessionRegistry.getAllSessions(principal, false);

        if (sessions.size() >= maxSession) {
            sessions.stream()
                    .min(Comparator.comparing(SessionInformation::getLastRequest))
                    .ifPresent(sessionInfo -> this.redisIndexedSessionRepository.deleteById(sessionInfo.getSessionId()));
        }
    }
}
