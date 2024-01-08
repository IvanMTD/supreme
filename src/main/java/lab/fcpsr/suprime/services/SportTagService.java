package lab.fcpsr.suprime.services;

import lab.fcpsr.suprime.dto.SportTagDTO;
import lab.fcpsr.suprime.models.AppUser;
import lab.fcpsr.suprime.models.Post;
import lab.fcpsr.suprime.models.SportTag;
import lab.fcpsr.suprime.repositories.SportTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class SportTagService{
    private final SportTagRepository sportTagRepository;
    private final PostService postService;

    public Mono<SportTag> save(SportTagDTO verifiedSportTag){
        SportTag sportTag = new SportTag(verifiedSportTag);
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

    public Flux<SportTagDTO> findAllByPostId(int postId){
        return postService.findById(postId)
                .flatMapMany(post -> sportTagRepository.findAllByIdIn(post.getSportTagIds())
                        .flatMap(sportTag -> {
                            SportTagDTO sportTagDTO = new SportTagDTO(sportTag);
                            return Mono.just(sportTagDTO);
                        }));
    }

    public Mono<Post> deletePostFromSportTags(Post post) {
        return sportTagRepository.findAllByIdIn(post.getSportTagIds())
                .flatMap(sportTag -> {
                    for(int id : sportTag.getPostIds()){
                        if(id == post.getId()){
                            sportTag.getPostIds().remove(post.getId());
                        }
                    }
                    return sportTagRepository.save(sportTag);
                })
                .collectList()
                .flatMap(sportTags -> Mono.just(post));
    }

    public Flux<SportTag> addUserInTags(AppUser user) {
        return sportTagRepository.findAllByIdIn(user.getSportTagIds()).flatMap(sportTag -> {
            sportTag.addUser(user);
            return sportTagRepository.save(sportTag);
        });
    }
}
