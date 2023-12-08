package lab.fcpsr.suprime.services;

import lab.fcpsr.suprime.dto.PostDTO;
import lab.fcpsr.suprime.models.Post;
import lab.fcpsr.suprime.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

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
}
