package lab.fcpsr.suprime.repositories;

import lab.fcpsr.suprime.models.SportTag;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.Set;

public interface SportTagRepository extends ReactiveCrudRepository<SportTag, Integer> {
    Flux<SportTag> findAllByIdIn(Set<Integer> ids);
}
