package lab.fcpsr.suprime.services;

import lab.fcpsr.suprime.dto.SportTagDTO;
import lab.fcpsr.suprime.models.Post;
import lab.fcpsr.suprime.models.SportTag;
import lab.fcpsr.suprime.repositories.SportTagRepository;
import lab.fcpsr.suprime.utils.CustomFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class SportTagService {
    private final SportTagRepository sportTagRepository;

    public Mono<SportTag> save(SportTagDTO verifiedSportTag){
        Path filePath = CustomFileUtil.prepareFilePath(verifiedSportTag.getFile().filename());
        verifiedSportTag.setImagePath(filePath.toString());
        verifiedSportTag.getFile().transferTo(filePath).subscribe();
        SportTag sportTag = new SportTag(verifiedSportTag);
        log.info(sportTag.toString());
        return sportTagRepository.save(sportTag);
    }

    public Mono<SportTag> findById(int id){
        return sportTagRepository.findById(id);
    }

    public Flux<SportTag> findAllByIds(Set<Integer> ids){
        return sportTagRepository.findAllByIdIn(ids);
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

    public Flux<SportTag> setupPost(Post post) {
        return sportTagRepository.findAllByIdIn(post.getSportTagIds())
                .flatMap(sportTag -> {
                    sportTag.addPost(post);
                    return sportTagRepository.save(sportTag);
                });
    }
}
