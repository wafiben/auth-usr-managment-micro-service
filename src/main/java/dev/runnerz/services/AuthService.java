package dev.runnerz.services;

import DTO.AuthResponse;
import DTO.LoginRequest;
import DTO.RegisterRequest;
import dev.runnerz.errors.InvalidCredentialsException;
import dev.runnerz.errors.UserAlreadyExistsException;
import dev.runnerz.errors.UserNotFoundException;
import dev.runnerz.models.User;
import dev.runnerz.repositories.UserRepository;
import dev.runnerz.security.JwtUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.project.event_managment.events.UserRegisteredEvent;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private RabbitTemplate rabbitTemplate;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       RabbitTemplate rabbitTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.rabbitTemplate = rabbitTemplate;
    }

    public AuthResponse register(RegisterRequest request) {

        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException();
        }

        User user = new User(request.getUsername(), request.getEmail(), request.getUsername(), passwordEncoder.encode(request.getPassword()));

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail());

        rabbitTemplate.convertAndSend(
                "user.event.exchange",
                "user.registered",
                new UserRegisteredEvent(savedUser.getId().toString(), savedUser.getEmail(), savedUser.getUsername())
        );

        rabbitTemplate.convertAndSend("test.queue", "Hello RabbitMQ Test Message!");
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token);
    }
}
