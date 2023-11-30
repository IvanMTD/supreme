package lab.fcpsr.suprime.repositories;

import lab.fcpsr.suprime.models.CategoryRole;
import org.springframework.data.repository.CrudRepository;
import reactor.core.publisher.Mono;

public interface CategoryRoleRepository extends CrudRepository<CategoryRole, Integer> {
    Mono<CategoryRole> findCategoryRoleByUserIdAndCategoryId(int userId, int categoryId);
}
