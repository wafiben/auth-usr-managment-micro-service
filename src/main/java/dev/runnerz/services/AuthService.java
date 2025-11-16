package dev.runnerz.services;

import DTO.AuthResponse;
import DTO.LoginRequest;
import DTO.RegisterRequest;
import dev.runnerz.models.User;
import dev.runnerz.repositories.UserRepository;
import dev.runnerz.security.JwtUtil;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }


    public AuthResponse register(RegisterRequest request) {

        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        System.out.println("ssssz " + existingUser);
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User(
                null,
                request.getEmail(),
                request.getUsername(),
                passwordEncoder.encode(request.getPassword())
        );

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token);
    }
}
