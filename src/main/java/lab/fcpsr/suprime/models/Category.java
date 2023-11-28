package lab.fcpsr.suprime.models;

import javafx.geometry.Pos;
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
    private @NonNull String name;
    private @NonNull String description;
    private Set<Integer> postIds = new HashSet<>();

    public void addPost(Post post){
        postIds.add(post.getId());
    }
}
