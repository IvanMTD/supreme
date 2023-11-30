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
public class Category {
    @Id
    private int id;

    private Set<Integer> postIds = new HashSet<>();

    private @NonNull String name;
    private @NonNull String description;
    private @NonNull String content;

    public void addPost(Post post){
        postIds.add(post.getId());
    }
}
