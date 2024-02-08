package lab.fcpsr.suprime.dto;

import lab.fcpsr.suprime.models.Post;
import lab.fcpsr.suprime.models.SportTag;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class TagPostDTO {
    private Post post;
    private List<SportTag> sportTags = new ArrayList<>();

    public void addTag(SportTag tag){
        sportTags.add(tag);
    }
}
