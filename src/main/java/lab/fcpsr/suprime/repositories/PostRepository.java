package lab.fcpsr.suprime.repositories;

import lab.fcpsr.suprime.models.Post;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PostRepository extends ReactiveCrudRepository<Post,Integer> {
}
