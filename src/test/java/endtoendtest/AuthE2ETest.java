package endtoendtest;

import dev.runnerz.RunnerzApplication;
import dev.runnerz.models.User;
import dev.runnerz.repositories.UserRepository;
import DTO.RegisterRequest;
import DTO.AuthResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes = RunnerzApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Transactional // automatically rolls back DB changes after each test
public class AuthE2ETest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testRegisterUser_Success() {
        User user = new User("Wafi", "example@example.com", "wafi", "securePassword");
        userRepository.save(user);

        User savedUser = userRepository.findById(user.getId()).orElse(null);

        assertNotNull(savedUser);
        assertEquals("example@example.com", savedUser.getEmail());
        assertEquals("Wafi", savedUser.getName());
        assertEquals("wafi", savedUser.getUsername());
    }

    @Test
    public void testRegisterEndpoint() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Wafi");
        request.setEmail("example@example.com");
        request.setUsername("wafi");
        request.setPassword("securePassword");

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                "/auth/register",
                request,
                AuthResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        User savedUser = userRepository.findByEmail("example@example.com").orElse(null);
        assertNotNull(savedUser);
        String hashedPassword = savedUser.getPassword();
        boolean matches = passwordEncoder.matches("securePassword", hashedPassword);
        assertTrue(matches);
    }
}
