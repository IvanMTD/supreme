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
public class Post {
    @Id
    private int id;

    private Set<Integer> fileIds = new HashSet<>();
    private int categoryId;

    private @NonNull String name;
    private @NonNull String annotation;
    private @NonNull String content;

    public void addFile(MinioFile file){
        fileIds.add(file.getId());
    }
}
