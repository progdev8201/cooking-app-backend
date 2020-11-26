package cal.controller;

import cal.model.dto.JwtResponseDTO;
import cal.model.dto.LoginFormDTO;
import cal.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("authenticate")
    public Object authenticate(@Valid @RequestBody LoginFormDTO loginFormDTO) {

        JwtResponseDTO response = authenticationService.authenticate(loginFormDTO);

        return response.getToken() == null ? ResponseEntity.badRequest() : response;
    }
}
