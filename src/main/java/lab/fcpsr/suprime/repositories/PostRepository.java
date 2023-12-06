package lab.fcpsr.suprime.repositories;

import lab.fcpsr.suprime.models.Post;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.Set;

public interface PostRepository extends ReactiveCrudRepository<Post, Integer> {
    Flux<Post> findAllByIdIn(Set<Integer> ids);
}
