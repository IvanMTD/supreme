package lab.fcpsr.suprime.repositories;

import lab.fcpsr.suprime.models.MinioFile;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.Set;

public interface MinioFileRepository extends ReactiveCrudRepository<MinioFile, Integer> {
    Flux<MinioFile> findAllByIdIn(Set<Integer> ids);
}
