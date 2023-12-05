package lab.fcpsr.suprime.validations;

import lab.fcpsr.suprime.dto.PostDTO;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

@Component
public class PostValidation implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(PostDTO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PostDTO post = (PostDTO) target;
        FilePart file = post.getFile();
        FilePart image = post.getImage();
        if(file == null) {
            errors.rejectValue("file", "", "Добавьте файл для загрузки");
        }else{
            if (file.filename().equals("")) {
                errors.rejectValue("file", "", "Добавьте файл для загрузки");
            }
        }
        if(image == null) {
            errors.rejectValue("image", "", "Добавьте изображение для загрузки");
        }else{
            if (image.filename().equals("")) {
                errors.rejectValue("image", "", "Добавьте изображение для загрузки");
            }
        }
    }

    public void checkTags(List<String> sportTagList, Errors errors) {
        if(sportTagList.size() == 0){
            errors.rejectValue("sportTagIds","","Укажите хотя бы один вид спорта");
        }
    }
}
