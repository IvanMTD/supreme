package lab.fcpsr.suprime.dto;

import lab.fcpsr.suprime.models.Post;
import lab.fcpsr.suprime.models.SportTag;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CategoryDTO {
    private SportTag sportTag = new SportTag();
    private List<Post> posts = new ArrayList<>();

    public void addPost(Post post){
        this.posts.add(post);
    }
}
