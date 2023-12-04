package lab.fcpsr.suprime.validations;

import lab.fcpsr.suprime.dto.PostDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PostValidation implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(PostDTO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PostDTO postDTO = (PostDTO) target;
    }
}
