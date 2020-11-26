package cal.model.dto;

import cal.validator.UnregisteredEmail;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class RegistrationFormDTO implements Serializable {
    @Email
    @UnregisteredEmail
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;


    public RegistrationFormDTO(){

    }

    public RegistrationFormDTO(@Email String email, @NotBlank String password, @NotBlank String firstName, @NotBlank String lastName) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
