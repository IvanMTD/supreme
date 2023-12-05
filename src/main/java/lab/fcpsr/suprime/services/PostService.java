package lab.fcpsr.suprime.services;

import lab.fcpsr.suprime.dto.PostDTO;
import lab.fcpsr.suprime.models.Post;
import lab.fcpsr.suprime.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public Mono<Post> save(PostDTO postDTO){
        Post post = new Post(postDTO);
        return postRepository.save(post);
    }
}
