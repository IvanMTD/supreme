package lab.fcpsr.suprime.services;

import lab.fcpsr.suprime.dto.CategoryDTO;
import lab.fcpsr.suprime.models.AppUser;
import lab.fcpsr.suprime.models.Post;
import lab.fcpsr.suprime.models.Role;
import lab.fcpsr.suprime.models.SportTag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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

    public Mono<Rendering> getMaterialsByRole(AppUser user){
        Flux<CategoryDTO> categories = Flux.empty();
        if(user != null){
            for(Role role : user.getRoles()){
                if(role.equals(Role.ADMIN)){
                    categories = sportTagService
                            .findAll()
                            .flatMap(sportTag -> {
                                CategoryDTO category = new CategoryDTO();
                                category.setSportTag(sportTag);
                                return postService
                                        .findAllByIds(sportTag.getPostIds())
                                        .collectList()
                                        .map(posts -> {
                                            category.setPosts(posts);
                                            return category;
                                        });
                            })
                            .log();
                }else if(role.equals(Role.MODERATOR)){
                    categories = userService.findByEmail(user.getMail())
                            .flatMapMany(u -> sportTagService.findAllByIds(u.getSportTagIds())
                                    .flatMap(sportTag -> {
                                        CategoryDTO category = new CategoryDTO();
                                        category.setSportTag(sportTag);
                                        return postService.findAllByIds(sportTag.getPostIds())
                                                .collectList()
                                                .map(posts -> {
                                                    category.setPosts(posts);
                                                    return category;
                                                });
                                    })
                            )
                            .log();
                }else if(role.equals(Role.PUBLISHER)){
                    categories = userService.findByEmail(user.getMail())
                            .flatMapMany(u -> {
                                log.info("USER: " + u.toString());
                                return sportTagService.findAllByIds(u.getSportTagIds())
                                        .flatMap(sportTag -> {
                                            CategoryDTO category = new CategoryDTO();
                                            category.setSportTag(sportTag);
                                            return postService.findAllByIds(sportTag.getPostIds())
                                                    .collectList()
                                                    .flatMap(posts -> {
                                                        List<Post> postList = new ArrayList<>();
                                                        for(Post post : posts){
                                                            if(u.getPostIds().stream().anyMatch(id -> post.getId() == id)){
                                                                postList.add(post);
                                                            }
                                                        }
                                                        category.setPosts(postList);
                                                        return Mono.just(category);
                                                    });
                                        });
                            })
                            .log();
                }
            }

            return Mono.just(Rendering
                    .view("template")
                    .modelAttribute("index","material-page")
                    .modelAttribute("categories",categories)
                    .build());
        }

        return Mono.just(Rendering
                .view("template")
                .modelAttribute("index","material-page")
                .modelAttribute("categories", Flux.empty())
                .build());
    }
}
