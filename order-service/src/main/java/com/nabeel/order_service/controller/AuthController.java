package com.nabeel.order_service.controller;

import com.nabeel.order_service.entity.User;
import com.nabeel.order_service.repository.UserRepository;
import com.nabeel.order_service.security.JwtTokenProvider;
import com.nabeel.order_service.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private WalletService walletService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(User.UserRole.TRADER)
                .isActive(true)
                .build();

        user = userRepository.save(user);

        // Create wallet for new user
        walletService.getOrCreateWallet(user.getId());

        String token = tokenProvider.generateToken(user.getUsername(), user.getId(), user.getRole().name());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getId().toString());
        response.put("username", user.getUsername());
        response.put("role", user.getRole().name());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }

        if (!user.getIsActive()) {
            return ResponseEntity.status(403).body(Map.of("error", "User account is inactive"));
        }

        String token = tokenProvider.generateToken(user.getUsername(), user.getId(), user.getRole().name());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getId().toString());
        response.put("username", user.getUsername());
        response.put("role", user.getRole().name());

        return ResponseEntity.ok(response);
    }

    static class RegisterRequest {
        private String username;
        private String email;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}

