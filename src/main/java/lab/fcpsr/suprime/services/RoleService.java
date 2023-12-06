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
            Flux<CategoryDTO> categoryFlux = null;

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
                    Flux<CategoryDTO> moderatorFlux = sportTagService.findAllByIds(user.getSportTagIds())
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
                    if(categoryFlux == null){
                        categoryFlux = moderatorFlux;
                    }else{
                        categoryFlux = merge(categoryFlux,moderatorFlux);
                    }
                }else if(role.equals(Role.PUBLISHER)){
                    Flux<CategoryDTO> postFlux = sportTagService.findAllByIds(user.getSportTagIds())
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

                    if(categoryFlux == null){
                        categoryFlux = postFlux;
                    }else {
                        categoryFlux = merge(categoryFlux,postFlux);
                    }
                }
            }

            return Mono.just(Rendering
                    .view("template")
                    .modelAttribute("index","material-page")
                    .modelAttribute("categories", categoryFlux == null ? Flux.empty() : categoryFlux)
                    .build());
        }

        return Mono.just(Rendering
                .view("template")
                .modelAttribute("index","material-page")
                .modelAttribute("categories", Flux.empty())
                .build());
    }

    private Flux<CategoryDTO> merge(Flux<CategoryDTO> flux1, Flux<CategoryDTO> flux2){
        return flux1
                .collectList()
                .flatMap(c -> {
                    return flux2
                            .collectList()
                            .map(pc -> {
                                int cSize = c.size();

                                for(int i=0; i<pc.size(); i++){
                                    boolean iCheck = false;
                                    int currentJ = 0;
                                    for(int j=0; j<cSize; j++){
                                        if(pc.get(i).getSportTag().getId() == c.get(j).getSportTag().getId()){
                                            iCheck = true;
                                            currentJ = j;
                                            break;
                                        }
                                    }
                                    if(iCheck){
                                        int pSize = c.get(currentJ).getPosts().size();
                                        for(int n=0; n<pc.get(i).getPosts().size(); n++){
                                            boolean nCheck = false;
                                            int currentK = 0;
                                            for(int k=0; k<pSize; k++){
                                                if(pc.get(i).getPosts().get(n).getId() == c.get(currentJ).getPosts().get(k).getId()){
                                                    nCheck = true;
                                                    currentK = k;
                                                    break;
                                                }
                                            }
                                            if(!nCheck){
                                                c.get(currentJ).getPosts().add(pc.get(n).getPosts().get(currentK));
                                            }
                                        }
                                    }else{
                                        c.add(pc.get(i));
                                    }
                                }

                                return c;
                            });
                })
                .flatMapMany(Flux::fromIterable);
    }
}
