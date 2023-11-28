package lab.fcpsr.suprime.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Post {
    @Id
    private int id;
    private @NonNull String name;
    private @NonNull String annotation;
    private @NonNull String content;
    private int categoryId;
    private int fileId;

    public void setCategory(Category category){
        categoryId = category.getId();
    }

    public void setFile(MinioFile file){
        fileId = file.getId();
    }
}
