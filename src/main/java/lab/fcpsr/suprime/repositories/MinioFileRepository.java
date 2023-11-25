package lab.fcpsr.suprime.repositories;

import lab.fcpsr.suprime.models.MinioFile;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface MinioFileRepository extends ReactiveCrudRepository<MinioFile,Integer> {
}
