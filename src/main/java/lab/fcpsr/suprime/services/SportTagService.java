package lab.fcpsr.suprime.services;

import lab.fcpsr.suprime.dto.SportTagDTO;
import lab.fcpsr.suprime.models.SportTag;
import lab.fcpsr.suprime.repositories.SportTagRepository;
import lab.fcpsr.suprime.utils.CustomFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class SportTagService {
    private final SportTagRepository sportTagRepository;

    public Mono<SportTag> save(SportTagDTO verifiedSportTag){
        String extension = CustomFileUtil.getExtension(verifiedSportTag.getFile().filename()).orElse("not");
        String randomWord = "";
        for(int i=0; i<20; i++){
            char ch = (char) Math.round(65 + (Math.random() * 25.0f));
            randomWord = randomWord + ch;
        }
        String uid = randomWord + "." + extension;
        Path filePath = Path.of("./src/main/resources/static/img/" + uid);
        verifiedSportTag.setImagePath(filePath.toString());
        verifiedSportTag.getFile().transferTo(filePath).subscribe();
        SportTag sportTag = new SportTag(verifiedSportTag);
        log.info(sportTag.toString());
        return sportTagRepository.save(sportTag);
    }

    public Flux<SportTag> findAll(){
        return sportTagRepository.findAll();
    }

    public Flux<SportTagDTO> findAllToDTO(){
        return sportTagRepository.findAll()
                .map(sportTag -> {
                    SportTagDTO sportTagDTO = new SportTagDTO(sportTag);
                    return sportTagDTO;
                });
    }
}
