package lab.fcpsr.suprime.models;

import lab.fcpsr.suprime.dto.PostDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Post {
    @Id
    private int id;
    private int userId;
    private Set<Integer> sportTagIds = new HashSet<>();
    private int fileId;
    private int imageId;

    private @NonNull String name;
    private @NonNull String annotation;
    private @NonNull String content;
    private @NonNull LocalDate placedAt;

    private boolean verified;
    private boolean allowed;

    public Post(PostDTO postDTO){
        setImageId(postDTO.getImageId());
        setName(postDTO.getName());
        setAnnotation(postDTO.getAnnotation());
        setContent(postDTO.getContent());
        setPlacedAt(LocalDate.now());

        setUserId(postDTO.getUserId());
        setSportTagIds(postDTO.getSportTagIds());
        setFileId(postDTO.getFileId());

        setVerified(false);
        setAllowed(false);
    }

    public void addSportTag(SportTag sportTag){
        this.sportTagIds.add(sportTag.getId());
    }

    public List<Integer> getSportTags(){
        List<Integer> ids = new ArrayList<>();
        for(Integer id : sportTagIds){
            ids.add(id);
        }
        return ids;
    }
}
