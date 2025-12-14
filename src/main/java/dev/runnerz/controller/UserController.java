package dev.runnerz.controller;


import dev.runnerz.errors.UserNotFoundException;
import dev.runnerz.models.User;
import dev.runnerz.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @GetMapping("/")
    public List<User> getAllUsers() {
        return  userRepository.findAll();
    }

    @GetMapping("/{id}")
    public User getOneUser(@PathVariable Long id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    user.setId(id);
                    return userRepository.save(user);
                })
                .orElseThrow(UserNotFoundException::new);
    }
}