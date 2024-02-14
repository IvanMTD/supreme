package lab.fcpsr.suprime.repositories;

import lab.fcpsr.suprime.models.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

public interface EventRepository extends ReactiveCrudRepository<Event,Integer> {
    Flux<Event> findAllByEndDateAfter(LocalDate date, Pageable pageable);
}
