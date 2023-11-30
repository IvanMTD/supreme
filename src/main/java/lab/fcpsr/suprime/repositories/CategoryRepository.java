package lab.fcpsr.suprime.repositories;

import lab.fcpsr.suprime.models.Category;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CategoryRepository extends ReactiveCrudRepository<Category,Integer> {
}
