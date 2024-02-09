package lab.fcpsr.suprime.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.http.codec.multipart.FilePart;

@Data
@RequiredArgsConstructor
public class SliderDTO {
    @NotBlank(message = "Укажите название")
    private String title;
    @NotBlank(message = "Укажите URL")
    @URL(message = "Не валидный URL")
    private String url;
    @NotNull(message = "Подгрузите файл")
    private FilePart image;
}
