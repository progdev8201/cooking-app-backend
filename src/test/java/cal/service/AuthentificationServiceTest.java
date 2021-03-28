package cal.service;

import cal.model.dto.JwtResponseDTO;
import cal.model.dto.LoginFormDTO;
import cal.model.entity.User;
import cal.repository.UserRepository;
import cal.security.JwtProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AuthentificationServiceTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    public void authenticate_validRequest() {

        // Arrange

        LoginFormDTO loginFormDTO = new LoginFormDTO("test@test.com", "123456789");
        User user = new User(UUID.randomUUID(), loginFormDTO.getEmail(), passwordEncoder.encode(loginFormDTO.getPassword()),"test","test", null);

        Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(Optional.of(user));

        // Act
        JwtResponseDTO response = authenticationService.authenticate(loginFormDTO);

        // Assert
        assertNotNull(response);

        assertEquals(user.getUniqueId(), response.getUserId());
        assertNotNull(response.getToken());
    }

    @Test
    public void authenticate_invalidRequest() {

        // Arrange

        LoginFormDTO loginFormDTO = new LoginFormDTO("test@test.com", "123456789");
        User user = new User(UUID.randomUUID(), loginFormDTO.getEmail(), passwordEncoder.encode("This is a different password then the request"),"test","test",null);

        Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(Optional.of(user));

        // Act & Assert
        JwtResponseDTO response = authenticationService.authenticate(loginFormDTO);

        assertNull(response);
    }
}
