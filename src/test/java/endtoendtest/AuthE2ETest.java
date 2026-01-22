package endtoendtest;

import dev.runnerz.RunnerzApplication;
import dev.runnerz.models.User;
import dev.runnerz.repositories.UserRepository;
import DTO.RegisterRequest;
import DTO.AuthResponse;
import dev.runnerz.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(
        classes = RunnerzApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Transactional
public class AuthE2ETest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private JwtUtil jwtUtil;

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

    @Test
    public void testGetProfileSuccessfully() {
        // Step 1: Register and get JWT token
        String token = registerAndGetToken("Wafi", "wafi@example.com", "opa", "securePassword");


        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<User> response = restTemplate.exchange(
                "/auth/profile",
                HttpMethod.GET,
                request,
                User.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("wafi@example.com", response.getBody().getEmail());
        assertEquals("Wafi", response.getBody().getName());
    }

    private String registerAndGetToken(String name, String email, String username, String password) {
        RegisterRequest request = new RegisterRequest();
        request.setName(name);
        request.setEmail(email);
        request.setUsername(username);
        request.setPassword(password);

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                "/auth/register",
                request,
                AuthResponse.class
        );

        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getToken());
        return response.getBody().getToken();
    }
}
