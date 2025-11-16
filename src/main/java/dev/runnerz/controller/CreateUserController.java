package dev.runnerz.controller;


import dev.runnerz.models.User;
import dev.runnerz.repositories.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class CreateUserController {

    private final UserRepository userRepository;

    public CreateUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return (User) userRepository.save(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        System.out.println("ssssssssss");
        return userRepository.findAll();
    }
}