package lab.fcpsr.suprime.dto;

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

    private FilePart image;
    private FilePart file;
    private String name;
    private String annotation;
    private String content;

    public void addSportTagId(int id){
        sportTagIds.add(id);
    }
}
