package dev.runnerz;

import builders.UserBuilder;
import dev.runnerz.eventservice.repositories.InMemoryEventServiceClient;
import dev.runnerz.repositories.InMemoryUserRepository;
import dev.runnerz.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import tests.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AuthTest {
    private InMemoryUserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private String secret = "uTckAxECoV0HWovKGSnKhaLvk0wcIm+Eo/deoWm2iqlwQxR3f4MA1vY4XMHSE/yJH8kAZhiKdsgAc6tLRLANpQ==";
    private long expiration = 86400000L;
    private JwtUtil jwtUtil;
    private InMemoryEventServiceClient eventServiceClient;

    @BeforeEach
    public void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        this.eventServiceClient = new InMemoryEventServiceClient();
        userRepository = new InMemoryUserRepository(eventServiceClient);
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

    @Test
    public void testGetProfile_AfterLogin() {
        User user = new UserBuilder()
                .id("1")
                .email("wafi@example.com")
                .username("wafi")
                .password(passwordEncoder.encode("securePassword"))
                .build();
        userRepository.save(user);

        User foundUser = userRepository.getProfile("wafi@example.com");
        assertNotNull(foundUser);
        assertEquals("wafi@example.com", foundUser.getEmail());
        assertEquals("wafi", foundUser.getUsername());
        assertEquals("1", foundUser.getId());
    }
}