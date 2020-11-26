package cal.service;

import cal.model.dto.RegistrationFormDTO;
import cal.model.entity.User;
import cal.model.enums.RoleName;
import cal.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class RegistrationServiceTest {
    @MockBean
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void registerUservalidRequest() {

        // Arrange
        RegistrationService service = new RegistrationService(userRepository, passwordEncoder);
        //todo find out why test passes even if we dont pass in a valid email
        RegistrationFormDTO registrationFormDTO = new RegistrationFormDTO("test", "123456789", "test", "test");

        // Act & assert
        Mockito.when(userRepository.save(Mockito.any())).then(inv -> {

            User user = (User) inv.getArgument(0);
            assertEquals(registrationFormDTO.getEmail(), user.getEmail());
            assertEquals(registrationFormDTO.getFirstName(), user.getFirstName());
            assertEquals(registrationFormDTO.getLastName(), user.getLastName());
            assertEquals(RoleName.CLIENT.toString(), user.getRole());
            assertTrue(passwordEncoder.matches(registrationFormDTO.getPassword(), user.getPassword()));

            return null;
        });

        service.registerUser(registrationFormDTO);
    }

}
