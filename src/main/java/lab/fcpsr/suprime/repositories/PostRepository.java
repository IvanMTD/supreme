package lab.fcpsr.suprime.repositories;

import lab.fcpsr.suprime.models.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface PostRepository extends ReactiveCrudRepository<Post, Integer> {
    Flux<Post> findAllByIdIn(Set<Integer> ids);
    Flux<Post> findAllByVerifiedIsFalse(Pageable pageable);
    Flux<Post> findAllByVerifiedIsFalse();
    Flux<Post> findAllByVerifiedTrueOrderByIdDesc(Pageable pageable);
    Flux<Post> findAllByAllowedTrueOrderByIdDesc(Pageable pageable);
    Flux<Post> findAllByVerifiedTrueOrderByIdDesc();
    Flux<Post> findAllByAllowedTrueOrderByIdDesc();
    Flux<Post> findAllByUserId(int userId);
    Mono<Post> findByIdAndVerifiedTrue(int id);
    Mono<Post> findByIdAndAllowedTrue(int id);
    Mono<Post> findByIdAndVerifiedFalse(int id);
    Flux<Post> findAllByUserIdAndVerifiedFalse(int userId, Pageable pageable);
    Flux<Post> findPostsByUserIdAndVerifiedFalseOrAllowedFalse(int userId, Pageable pageable);
    Flux<Post> findPostsByVerifiedTrueAndAllowedFalse(Pageable pageable);
    Flux<Post> findAllByUserIdAndVerifiedFalse(int userId);
    Flux<Post> findAllByContentContainsIgnoreCaseAndVerifiedIsTrue(String search);
    @Query("select * from post where :userId = any(user_save_list)")
    Mono<Post> findPostWhereUserIdInUserSaveList(@Param("userId") Integer userId);
    @Query("select * from post where post.sport_tag_ids && :ids and verified = false")
    Flux<Post> findAllBySportTagIdsAny(@Param("ids") Integer[] ids);
}
