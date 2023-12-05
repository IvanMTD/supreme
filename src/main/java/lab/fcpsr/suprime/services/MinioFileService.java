package lab.fcpsr.suprime.services;

import lab.fcpsr.suprime.models.MinioFile;
import lab.fcpsr.suprime.models.Post;
import lab.fcpsr.suprime.repositories.MinioFileRepository;
import lab.fcpsr.suprime.templates.MinioResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class MinioFileService {
    private final MinioFileRepository fileRepository;

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
        return fileRepository.findById(id)
                .publishOn(Schedulers.boundedElastic())
                .map(minioFile -> {
                    MinioFile file = new MinioFile(minioFile);
                    fileRepository.deleteById(id).subscribe();
                    return file;
                });
    }

    public Flux<MinioFile> setupPost(Post post) {
        return fileRepository.findAllByIdIn(post.getFileIds())
                .flatMap(file -> {
                    file.addPost(post);
                    return fileRepository.save(file);
                });
    }
}
