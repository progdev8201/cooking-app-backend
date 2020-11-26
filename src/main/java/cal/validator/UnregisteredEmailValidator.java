package cal.validator;

import cal.repository.UserRepository;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class UnregisteredEmailValidator implements ConstraintValidator<UnregisteredEmail, String> {

    //
    // Dependencies
    //

    private final UserRepository userRepository;

    //
    // Constructors
    //

    public UnregisteredEmailValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //
    // Validation
    //

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return userRepository.findByEmail(s).isEmpty();
    }

}
