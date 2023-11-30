package lab.fcpsr.suprime.repositories;

import lab.fcpsr.suprime.models.PostRole;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface PostRoleRepository extends ReactiveCrudRepository<PostRole, Integer> {
    Mono<PostRole> findPostRoleByUserIdAndPostId(int userId, int postId);
}
