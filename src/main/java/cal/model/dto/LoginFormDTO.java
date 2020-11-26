package cal.model.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class LoginFormDTO implements Serializable {
    @Email
    private String email;

    @NotBlank
    private String password;

    public LoginFormDTO(){

    }

    public LoginFormDTO(@Email String email, @NotBlank String password) {
        this.email = email;
        this.password = password;
    }
}
