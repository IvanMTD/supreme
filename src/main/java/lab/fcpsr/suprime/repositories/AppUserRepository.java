package lab.fcpsr.suprime.repositories;

import lab.fcpsr.suprime.models.AppUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface AppUserRepository extends ReactiveCrudRepository<AppUser, Integer> {
    Mono<AppUser> findByMail(String mail);
    Flux<AppUser> findAllByIdIn(Set<Integer> userSaveList);
}
