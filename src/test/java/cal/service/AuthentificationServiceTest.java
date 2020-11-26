package cal.service;

import cal.model.dto.JwtResponseDTO;
import cal.model.dto.LoginFormDTO;
import cal.model.entity.User;
import cal.repository.UserRepository;
import cal.security.JwtProvider;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AuthentificationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void authenticate_validRequest() {

        // Arrange
        AuthenticationService authenticationService = new AuthenticationService(userRepository, jwtProvider, passwordEncoder);

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
        AuthenticationService service = new AuthenticationService(userRepository, jwtProvider, passwordEncoder);

        LoginFormDTO loginFormDTO = new LoginFormDTO("test@test.com", "123456789");
        User user = new User(UUID.randomUUID(), loginFormDTO.getEmail(), passwordEncoder.encode("This is a different password then the request"),"test","test",null);

        Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(Optional.of(user));

        // Act & Assert
        JwtResponseDTO response = service.authenticate(loginFormDTO);

        assertNull(response);
    }
}
