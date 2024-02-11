package lab.fcpsr.suprime.validations;

import lab.fcpsr.suprime.dto.SliderDTO;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class SliderValidation implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(SliderDTO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SliderDTO slider = (SliderDTO) target;
        FilePart image = slider.getImage();
        if(image == null) {
            errors.rejectValue("image", "", "Добавьте изображение для загрузки");
        }else{
            if (image.filename().equals("")) {
                errors.rejectValue("image", "", "Добавьте изображение для загрузки");
            }
        }
    }
}
