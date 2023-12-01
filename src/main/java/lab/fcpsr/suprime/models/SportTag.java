package lab.fcpsr.suprime.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class SportTag {
    @Id
    private int id;
    private Set<Integer> userIds = new HashSet<>();
    private Set<Integer> postIds = new HashSet<>();

    private @NonNull String imagePath;
    private @NonNull String name;
    private @NonNull String description;

    public void addUser(AppUser user){
        this.userIds.add(user.getId());
    }

    public void addPost(Post post){
        this.postIds.add(post.getId());
    }
}
