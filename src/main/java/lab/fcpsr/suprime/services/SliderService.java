package lab.fcpsr.suprime.services;

import lab.fcpsr.suprime.repositories.SliderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SliderService {
    private final SliderRepository sliderRepository;
}
