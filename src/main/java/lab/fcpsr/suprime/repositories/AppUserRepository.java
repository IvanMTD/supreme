package lab.fcpsr.suprime.repositories;

import lab.fcpsr.suprime.models.AppUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface AppUserRepository extends ReactiveCrudRepository<AppUser, Integer> {
    Mono<AppUser> findByMail(String mail);
}
