package lab.fcpsr.suprime.services;

import ch.qos.logback.classic.spi.LoggingEventVO;
import lab.fcpsr.suprime.dto.EventDTO;
import lab.fcpsr.suprime.models.Event;
import lab.fcpsr.suprime.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    public Mono<Event> saveDto(EventDTO eventDTO) {
        return eventRepository.save(new Event(eventDTO));
    }

    public Flux<Event> getAll() {
        return eventRepository.findAll();
    }

    public Mono<Event> deleteEvent(int eventId) {
        return eventRepository.findById(eventId).flatMap(event -> eventRepository.delete(event).then(Mono.just(event)));
    }

    public Flux<Event> getAllActual(Pageable pageable) {
        return eventRepository.findAllByEndDateAfter(LocalDate.now(),pageable);
    }

    public Mono<Event> getEventById(int id) {
        return eventRepository.findById(id);
    }
}
