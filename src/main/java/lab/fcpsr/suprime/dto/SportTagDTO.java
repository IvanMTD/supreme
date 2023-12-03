package lab.fcpsr.suprime.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lab.fcpsr.suprime.models.SportTag;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;

@Data
@NoArgsConstructor
public class SportTagDTO {
    private int id;
    private FilePart file;
    private String imagePath;
    @NotBlank(message = "Название вида спорта не может быть пустым")
    private String name;
    @NotBlank(message = "Описание вида спорта не может быть пустым")
    private String description;

    public SportTagDTO(SportTag sportTag){
        setId(sportTag.getId());
        setImagePath(sportTag.getImagePath());
        setName(sportTag.getName());
        setDescription(sportTag.getDescription());
    }
}
