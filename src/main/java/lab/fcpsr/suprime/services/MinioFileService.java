package lab.fcpsr.suprime.services;

import lab.fcpsr.suprime.models.MinioFile;
import lab.fcpsr.suprime.models.Post;
import lab.fcpsr.suprime.repositories.MinioFileRepository;
import lab.fcpsr.suprime.repositories.PostRepository;
import lab.fcpsr.suprime.templates.MinioResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioFileService {
    private final MinioFileRepository fileRepository;
    private final PostRepository postRepository;

    public Mono<MinioFile> save(MinioResponse response){
        MinioFile minioFile = new MinioFile();
        minioFile.setUid(response.getUid());
        minioFile.setName(response.getOriginalFileName());
        minioFile.setType(response.getType());
        minioFile.setETag(response.getResponse().etag());
        minioFile.setBucket(response.getResponse().bucket());
        minioFile.setPath(response.getResponse().object());
        minioFile.setMinioUrl(response.getResponse().region() != null ? response.getResponse().region() : "no url");
        minioFile.setFileSize(response.getFileSize());
        return fileRepository.save(minioFile);
    }

    public Mono<MinioFile> findById(int id){
        return fileRepository.findById(id);
    }

    public Flux<MinioFile> findAll(){
        return fileRepository.findAll();
    }

    public Mono<MinioFile> deleteById(int id){
        return fileRepository.findById(id).flatMap(minioFile -> fileRepository.deleteById(id).then(Mono.just(minioFile)));
    }

    public Mono<MinioFile> setupPost(Post post) {
        return fileRepository.findById(post.getFileId()).flatMap(file -> {
                    file.addPost(post);
                    return fileRepository.save(file);
                });
    }

    public Mono<MinioFile> findByPostId(int id) {
        return postRepository.findById(id)
                .flatMap(post -> fileRepository.findById(post.getFileId()));
    }

    public Mono<MinioFile> deletePostData(int dataId) {
        return deleteById(dataId);
    }
}
