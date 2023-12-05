package lab.fcpsr.suprime.models;

import lab.fcpsr.suprime.dto.PostDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Post {
    @Id
    private int id;
    private int userId;
    private Set<Integer> sportTagIds = new HashSet<>();
    private Set<Integer> fileIds = new HashSet<>();

    private @NonNull String imagePath;
    private @NonNull String name;
    private @NonNull String annotation;
    private @NonNull String content;
    private @NonNull Date placedAt;

    public Post(PostDTO postDTO){

    }

    public void setUser(AppUser user){
        this.userId = user.getId();
    }

    public void addSportTag(SportTag sportTag){
        this.sportTagIds.add(sportTag.getId());
    }

    public void addFile(MinioFile file){
        this.fileIds.add(file.getId());
    }
}
