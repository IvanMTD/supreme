package lab.fcpsr.suprime.dto;

import jakarta.validation.constraints.NotBlank;
import lab.fcpsr.suprime.models.Post;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;

import java.time.LocalDate;
import java.util.*;

@Data
@NoArgsConstructor
public class PostDTO {
    private int id;
    private int userId;
    private Set<Integer> sportTagIds = new HashSet<>();
    private int fileId;
    private int imageId;

    private FilePart image;
    private FilePart file;
    @NotBlank(message = "Введите название")
    private String name;
    @NotBlank(message = "Добавьте аннотацию")
    private String annotation;
    @NotBlank(message = "Заполните статью")
    private String content;
    private LocalDate placedAt;

    public PostDTO(Post post){
        setId(post.getId());
        setUserId(post.getUserId());
        setSportTagIds(post.getSportTagIds());
        setFileId(post.getFileId());
        setImageId(post.getImageId());
        setName(post.getName());
        setAnnotation(post.getAnnotation());
        setContent(post.getContent());
        setPlacedAt(post.getPlacedAt());
    }

    public void addSportTagId(int id){
        sportTagIds.add(id);
    }
}
