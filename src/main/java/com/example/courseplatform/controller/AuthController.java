package com.example.courseplatform.controller;

import com.example.courseplatform.dto.AuthResponse;
import com.example.courseplatform.dto.LoginRequest;
import com.example.courseplatform.dto.RegisterRequest;
import com.example.courseplatform.dto.RegisterResponseDTO;
import com.example.courseplatform.model.User;
import com.example.courseplatform.repository.UserRepository;
import com.example.courseplatform.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Value("${app.jwt.expiration}")
    private int jwtExpirationMs;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
            PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        return ResponseEntity.ok(new AuthResponse(jwt, loginRequest.getEmail(), jwtExpirationMs / 1000));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return new ResponseEntity<>(Map.of("error", "Conflict", "message", "Email already in use!"),
                    HttpStatus.CONFLICT);
        }

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        User savedUser = userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterResponseDTO(
                savedUser.getId(),
                savedUser.getEmail(),
                "User registered successfully"));
    }
}
