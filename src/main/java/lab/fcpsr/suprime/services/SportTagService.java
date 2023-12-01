package lab.fcpsr.suprime.services;

import lab.fcpsr.suprime.models.SportTag;
import lab.fcpsr.suprime.repositories.SportTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class SportTagService {
    private final SportTagRepository sportTagRepository;

    public Flux<SportTag> findAll(){
        return sportTagRepository.findAll();
    }
}
