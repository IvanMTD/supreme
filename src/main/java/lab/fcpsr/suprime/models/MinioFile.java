package lab.fcpsr.suprime.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class MinioFile {
    @Id
    private int id;

    private int postId;

    private @NonNull String uid;
    private @NonNull String name;
    private @NonNull String type;
    private @NonNull String eTag;
    private @NonNull String bucket;
    private @NonNull String path;
    private @NonNull String minioUrl;
    private int fileSize;

    public MinioFile(MinioFile minioFile){
        setId(minioFile.getId());
        setPostId(minioFile.getPostId());
        setUid(minioFile.getUid());
        setName(minioFile.getName());
        setType(minioFile.getType());
        setETag(minioFile.getETag());
        setBucket(minioFile.getBucket());
        setPath(minioFile.getPath());
        setMinioUrl(minioFile.getMinioUrl());
        setFileSize(minioFile.getFileSize());
    }

    public void addPost(Post post){
        postId = post.getId();
    }
}
