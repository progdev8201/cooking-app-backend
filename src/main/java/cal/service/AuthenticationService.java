package cal.service;

import cal.model.dto.JwtResponseDTO;
import cal.model.dto.LoginFormDTO;
import cal.model.entity.User;
import cal.repository.UserRepository;
import cal.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@Service
@Validated
public class AuthenticationService {

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private  JwtProvider jwtProvider;

    @Autowired
    private  PasswordEncoder passwordEncoder;

    // SERVICES

    public JwtResponseDTO authenticate(@Valid final LoginFormDTO loginFormDTO) {
        final Optional<User> user = userRepository.findByEmail(loginFormDTO.getEmail());

        if (user.isPresent() && passwordEncoder.matches(loginFormDTO.getPassword(), user.get().getPassword())) {

            final String token = jwtProvider.generate(user.get());
            final UUID userId = user.get().getUniqueId();

            return new JwtResponseDTO(userId, token);
        }

        return null;
    }

}
