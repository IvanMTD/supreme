package lab.fcpsr.suprime.services;

import lab.fcpsr.suprime.dto.PostDTO;
import lab.fcpsr.suprime.models.MinioFile;
import lab.fcpsr.suprime.models.Post;
import lab.fcpsr.suprime.repositories.MinioFileRepository;
import lab.fcpsr.suprime.repositories.PostRepository;
import lab.fcpsr.suprime.utils.CustomFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final MinioFileService fileService;
    private final MinioService minioService;

    public Mono<Post> save(PostDTO postDTO){
        Post post = new Post(postDTO);
        return postRepository.save(post);
    }

    public Mono<Post> findById(int id){
        return postRepository.findById(id);
    }

    public Mono<PostDTO> findByIdDTO(int id){
        return postRepository.findById(id)
                .map(post -> {
                    PostDTO postDTO = new PostDTO(post);
                    return postDTO;
                });
    }

    public Flux<Post> findAll() {
        return postRepository.findAll();
    }

    public Flux<Post> findAllByIds(Set<Integer> ids){
        return postRepository.findAllByIdIn(ids);
    }

    public Mono<Post> updatePost(PostDTO postDTO, int id) {
        return postRepository.findById(id)
                .flatMap(post -> {
                    log.info("Before: " + post.toString());
                    post.setName(postDTO.getName());
                    post.setAnnotation(postDTO.getAnnotation());
                    post.setContent(postDTO.getContent());
                    log.info("After: " + post);
                    return Mono.just(post);
                })
                .publishOn(Schedulers.boundedElastic())
                .flatMap(post -> {
                    if(postDTO.getImage() != null){
                        try {
                            Files.delete(Path.of(postDTO.getImagePath()));
                            Path path = CustomFileUtil.prepareFilePath(postDTO.getImage().filename());
                            post.setImagePath(path.toString());
                            postDTO.getImage().transferTo(path).log().subscribe();
                        } catch (IOException e) {
                            return Mono.error(new RuntimeException(e));
                        }
                    }
                    return Mono.just(post);
                })
                .flatMap(post -> {
                    if(postDTO.getFile() != null){
                        int fileId = 0;
                        for(int fId : post.getFileIds()){
                            fileId = fId;
                            break;
                        }
                        return fileService.deleteById(fileId)
                                .publishOn(Schedulers.boundedElastic())
                                .flatMap(fileInfo -> {
                                    minioService.delete(fileInfo).subscribe();
                                    return minioService
                                            .uploadStream(postDTO.getFile())
                                            .flatMap(response -> fileService.save(response)
                                                    .doOnNext(mf -> log.info("file saved in data_db witch id " + mf.getId()))
                                                    .flatMap(mf -> {
                                                        post.setFileIds(new HashSet<>(Set.of(mf.getId())));
                                                        return postRepository.save(post);
                                                    })
                                            )
                                            .log();
                                });
                    }
                    return postRepository.save(post);
                });
    }

    public Mono<Post> deletePost(int id) {
        return postRepository.findById(id)
                .flatMap(post -> postRepository.deleteById(id)
                        .then(Mono.just(post)));
    }
}
