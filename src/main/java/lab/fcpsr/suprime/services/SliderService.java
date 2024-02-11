package lab.fcpsr.suprime.services;

import lab.fcpsr.suprime.models.Slider;
import lab.fcpsr.suprime.repositories.SliderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SliderService {
    private final SliderRepository sliderRepository;
    public Mono<Slider> save(Slider slider) {
        return sliderRepository.save(slider);
    }
    public Flux<Slider> getAll() {
        return sliderRepository.findAll();
    }

    public Mono<Slider> delete(int id) {
        return sliderRepository.findById(id).flatMap(slider -> sliderRepository.delete(slider).then(Mono.just(slider)));
    }
}
