package lab.fcpsr.suprime.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;

import java.util.*;

@Data
@NoArgsConstructor
public class PostDTO {
    private int id;
    private int userId;
    private Set<Integer> sportTagIds = new HashSet<>();
    private Set<Integer> fileIds = new HashSet<>();

    private String imagePath;
    private FilePart image;
    private FilePart file;
    @NotBlank(message = "Введите название")
    private String name;
    @NotBlank(message = "Добавьте аннотацию")
    private String annotation;
    @NotBlank(message = "Заполните статью")
    private String content;

    public void addSportTagId(int id){
        sportTagIds.add(id);
    }
    public void addFileId(int id){
        fileIds.add(id);
    }
}
