package lab.fcpsr.suprime.repositories;

import lab.fcpsr.suprime.models.AppUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AppUserRepository extends ReactiveCrudRepository<AppUser, Integer> {
}
