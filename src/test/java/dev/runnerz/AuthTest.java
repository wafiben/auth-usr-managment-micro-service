package dev.runnerz;

import builders.UserBuilder;
import dev.runnerz.repositories.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tests.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthTest {

    private InMemoryUserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository = new InMemoryUserRepository();
    }

    @Test
    public void testRegister_Success() {

        User user = new UserBuilder()
                .id("1")
                .email("example@example.com")
                .username("wafi")
                .password("securePassword")
                .build();

        userRepository.save(user);

        var foundUser = userRepository.findOne("1");
        assertEquals("example@example.com", foundUser.getEmail());
    }
}
