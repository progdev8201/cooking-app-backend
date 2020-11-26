package cal.controller;

import cal.model.dto.RegistrationFormDTO;
import cal.service.RegistrationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/registration")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService){
        this.registrationService = registrationService;
    }

    @PostMapping()
    public void registerStudent(@Valid @RequestBody RegistrationFormDTO registrationFormDTO) {
        registrationService.registerUser(registrationFormDTO);
    }
}
