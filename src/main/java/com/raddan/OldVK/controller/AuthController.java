package com.raddan.OldVK.controller;

import com.raddan.OldVK.dto.AuthDTO;
import com.raddan.OldVK.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Public APIs called when registering a new user
     *
     * @param authDTO
     * @return ResponseEntity
     **/
    @PostMapping(path = "/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthDTO authDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.authService.register(authDTO));
    }

    /**
     * Public API that allows user to login
     *
     * @param authDTO
     * @param request
     * @param response
     * @return AuthResponse
     **/
    @PostMapping(path = "/login")
    public ResponseEntity<?> loginUser(
            @Valid @RequestBody AuthDTO authDTO,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return new ResponseEntity<>(authService.login(authDTO, request, response), HttpStatus.OK);
    }

    /**
     * Protected route only users with the role ADMIN can hit
     *
     * @param authentication
     * @return String
     **/
    @GetMapping(path = "/authenticated")
    @PreAuthorize(value = "hasAuthority('ADMIN')")
    public String getAuthenticated(Authentication authentication) {
        return "Admin name is " + authentication.getName();
    }

    /**
     * Protected route. Any authenticated user can hit this
     *
     * @param authentication
     * @return String
     **/
    @GetMapping(path = "/user")
    public String getUserProfile(Authentication authentication) {
        return "This is secured rout." + "\n" + "Username: " + authentication.getName();
    }
}
