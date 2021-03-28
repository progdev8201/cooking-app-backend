package cal.service;

import cal.model.dto.RegistrationFormDTO;
import cal.model.entity.Routine;
import cal.model.entity.User;
import cal.model.enums.RoleName;
import cal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

@Service
@Validated
public class RegistrationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Logger LOGGER = Logger.getLogger(RegistrationService.class.getName());

    // SERVICES

    public void registerUser(@Valid RegistrationFormDTO registrationFormDTO) {
        User user = new User(UUID.randomUUID(), registrationFormDTO.getEmail(), passwordEncoder.encode(registrationFormDTO.getPassword()), registrationFormDTO.getFirstName(), registrationFormDTO.getLastName(), RoleName.CLIENT.toString());
        Routine routine = new Routine(UUID.randomUUID(), "base", new ArrayList<>());

        user.getRoutines().add(routine);

        userRepository.save(user);

        LOGGER.info("CLIENT REGISTRATION SUCCESS!");
    }

}
