package lab.fcpsr.suprime.services;

import lab.fcpsr.suprime.dto.CategoryDTO;
import lab.fcpsr.suprime.models.AppUser;
import lab.fcpsr.suprime.models.Post;
import lab.fcpsr.suprime.models.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service("RoleService")
@RequiredArgsConstructor
public class RoleService {

    private final SportTagService sportTagService;
    private final PostService postService;

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

    public Mono<Rendering> getMaterialsByRole(AppUser user){
        if(user != null){
            Flux<CategoryDTO> categoryFlux = Flux.empty();

            for(Role role : user.getRoles()){
                if(role.equals(Role.ADMIN)){
                    categoryFlux = sportTagService.findAll()
                            .flatMap(sportTag -> {
                                CategoryDTO category = new CategoryDTO();
                                category.setSportTag(sportTag);
                                return postService.findAllByIds(sportTag.getPostIds())
                                        .collectList()
                                        .map(posts -> {
                                            category.setPosts(posts);
                                            return category;
                                        });
                            });
                }else if(role.equals(Role.MODERATOR)){
                    categoryFlux = sportTagService.findAllByIds(user.getSportTagIds())
                            .flatMap(sportTag -> {
                                CategoryDTO category = new CategoryDTO();
                                category.setSportTag(sportTag);
                                return postService.findAllByIds(sportTag.getPostIds())
                                        .collectList()
                                        .map(posts -> {
                                            category.setPosts(posts);
                                            return category;
                                        });
                            });
                }else if(role.equals(Role.PUBLISHER)){
                    categoryFlux = sportTagService.findAllByIds(user.getSportTagIds())
                            .flatMap(sportTag -> {
                                CategoryDTO category = new CategoryDTO();
                                category.setSportTag(sportTag);
                                return postService.findAllByIds(sportTag.getPostIds())
                                        .collectList()
                                        .map(posts -> {
                                            List<Post> postList = new ArrayList<>();
                                            for(Post post : posts){
                                                if(user.getPostIds().stream().anyMatch(id -> post.getId() == id)){
                                                    postList.add(post);
                                                }
                                            }
                                            category.setPosts(postList);
                                            return category;
                                        });
                            });
                }
            }

            return Mono.just(Rendering
                    .view("template")
                    .modelAttribute("index","material-page")
                    .modelAttribute("categories", categoryFlux)
                    .build());
        }

        return Mono.just(Rendering
                .view("template")
                .modelAttribute("index","material-page")
                .modelAttribute("categories", Flux.empty())
                .build());
    }
}
