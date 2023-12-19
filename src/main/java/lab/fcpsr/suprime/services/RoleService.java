package lab.fcpsr.suprime.services;

import lab.fcpsr.suprime.models.AppUser;
import lab.fcpsr.suprime.models.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service("RoleService")
@RequiredArgsConstructor
public class RoleService {

    private final SportTagService sportTagService;
    private final PostService postService;
    private final AppReactiveUserDetailService userService;

    public boolean isAdmin(AppUser user){
        if(user != null) {
            for (Role role : user.getRoles()) {
                if (role.equals(Role.ADMIN)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isModerator(AppUser user){
        if(user != null){
            for(Role role : user.getRoles()){
                if(role.equals(Role.MODERATOR)){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isPublisher(AppUser user){
        if(user != null){
            for(Role role : user.getRoles()){
                if(role.equals(Role.PUBLISHER)){
                    return true;
                }
            }
        }
        return false;
    }

    public Mono<Boolean> getAccess(AppUser user, int postId){
        if(user != null){
            return userService.findByEmail(user.getMail())
                    .flatMap(u -> {
                        for(Role role : u.getRoles()){
                            if(role.equals(Role.ADMIN)){
                                return Mono.just(true);
                            }else if(role.equals(Role.MODERATOR)){
                                return postService.findById(postId)
                                        .flatMap(post -> sportTagService.findAll()
                                                .filter(sportTag -> post.getSportTagIds().stream().anyMatch(id -> id == sportTag.getId()))
                                                .collectList()
                                                .flatMap(list -> {
                                                    if(list.size() != 0){
                                                        return Mono.just(true);
                                                    }else{
                                                        return Mono.just(false);
                                                    }
                                                })
                                                .defaultIfEmpty(false));
                            }else if(role.equals(Role.PUBLISHER)){
                                return postService.findById(postId)
                                        .filter(post -> u.getPostIds().stream().anyMatch(id -> id == postId))
                                        .flatMap(post -> Mono.just(true))
                                        .defaultIfEmpty(false);
                            }
                        }
                        return Mono.just(false);
                    });
        }
        return Mono.just(false);
    }
}
