package lab.fcpsr.suprime.services;

import lab.fcpsr.suprime.dto.PostDTO;
import lab.fcpsr.suprime.models.AppUser;
import lab.fcpsr.suprime.models.MinioFile;
import lab.fcpsr.suprime.models.Post;
import lab.fcpsr.suprime.models.Role;
import lab.fcpsr.suprime.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public Mono<Post> save(PostDTO postDTO){
        Post post = new Post(postDTO);
        return postRepository.save(post);
    }

    public Mono<Post> save(Post post){
        return postRepository.save(post);
    }

    public Mono<Post> findById(int id){
        return postRepository.findById(id);
    }

    public Mono<PostDTO> findByIdDTO(int id){
        return postRepository.findById(id)
                .map(post -> {
                    PostDTO postDTO = new PostDTO(post);
                    return postDTO;
                });
    }

    public Flux<Post> findAll() {
        return postRepository.findAll();
    }

    public Flux<Post> findAllByIds(Set<Integer> ids){
        return postRepository.findAllByIdIn(ids);
    }

    public Mono<Post> updatePost(int id, PostDTO postDTO, Mono<MinioFile> image, Mono<MinioFile> file){
        return postRepository.findById(id).flatMap(post -> {
            if(image != null){
                return image.flatMap(i -> {
                    post.setImageId(i.getId());
                    return Mono.just(post);
                });
            }
            return Mono.just(post);
        }).flatMap(post -> {
            if(file != null){
                return file.flatMap(f -> {
                    post.setFileId(f.getId());
                    return Mono.just(post);
                });
            }
            return Mono.just(post);
        }).flatMap(post -> {
            post.setName(postDTO.getName());
            post.setAnnotation(postDTO.getAnnotation());
            post.setContent(postDTO.getContent());
            return postRepository.save(post);
        });
    }

    public Mono<Post> deletePost(int id) {
        return postRepository.findById(id)
                .flatMap(post -> postRepository.deleteById(id).then(Mono.just(post)));
    }

    public Flux<Post> findPostsByUserRole(AppUser user, Pageable pageable) {
        for(Role role : user.getRoles()){
            if(role.equals(Role.ADMIN)){
                return postRepository.findAllByVerifiedIsFalse(pageable);
            }else if(role.equals(Role.MAIN_MODERATOR)){
                return postRepository.findPostsByVerifiedTrueAndAllowedFalse(pageable);
            }else if(role.equals(Role.MODERATOR)){
                List<Integer> sportTagIds = new ArrayList<>(user.getSportTagIds());
                Integer[] ids = new Integer[sportTagIds.size()];
                for(int i=0; i< sportTagIds.size(); i++){
                    ids[i] = sportTagIds.get(i);
                }
                return postRepository.findAllBySportTagIdsAny(ids)
                        .collectList()
                        .flatMapMany(list -> {
                            int start = pageable.getPageNumber() * pageable.getPageSize();
                            int end = start + pageable.getPageSize();
                            List<Post> posts = new ArrayList<>();
                            for(int i=start; i<end; i++){
                                if(i < list.size()) {
                                    posts.add(list.get(i));
                                }
                            }
                            return Flux.fromIterable(posts);
                        });
            }else if(role.equals(Role.PUBLISHER)){
                return postRepository.findPostsByUserIdAndVerifiedFalseOrAllowedFalse(user.getId(), pageable).doOnNext(p -> {
                    log.info(p.toString());
                });
            }else{
                return Flux.empty();
            }
        }
        return Flux.empty();
    }

    public Mono<Integer> findPostsByUserRoleGetLastPage(AppUser user, int itemOnPage) {
        for(Role role : user.getRoles()){
            if(role.equals(Role.ADMIN)){
                return postRepository.findAllByVerifiedIsFalse()
                        .collectList()
                        .flatMap(list -> Mono.just(getLastPage(list.size(),itemOnPage)));
            }else if(role.equals(Role.MAIN_MODERATOR)){
                return postRepository.findAllByVerifiedIsFalse()
                        .collectList()
                        .flatMap(list -> Mono.just(getLastPage(list.size(),itemOnPage)));
            }else if(role.equals(Role.MODERATOR)){
                List<Integer> sportTagIds = new ArrayList<>(user.getSportTagIds());
                Integer[] ids = new Integer[sportTagIds.size()];
                for(int i=0; i< sportTagIds.size(); i++){
                    ids[i] = sportTagIds.get(i);
                }
                return postRepository.findAllBySportTagIdsAny(ids)
                        .collectList()
                        .flatMap(list -> Mono.just(getLastPage(list.size(),itemOnPage)));
            }else if(role.equals(Role.PUBLISHER)){
                return postRepository.findAllByUserIdAndVerifiedFalse(user.getId())
                        .collectList()
                        .flatMap(list -> Mono.just(getLastPage(list.size(),itemOnPage)));
            }
        }
        return Mono.just(0);
    }

    public Flux<Post> findAllVerified(Pageable pageable) {
        return postRepository.findAllByVerifiedTrueOrderByIdDesc(pageable);
    }

    public Flux<Post> findAllAllowed(Pageable pageable){
        return postRepository.findAllByAllowedTrueOrderByIdDesc(pageable);
    }

    public Mono<Integer> findAllVerifiedLastPage(int itemOnPage) {
        return postRepository.findAllByVerifiedTrueOrderByIdDesc()
                .collectList()
                .flatMap(list -> Mono.just(getLastPage(list.size(), itemOnPage)));
    }

    public Mono<Integer> findAllAllowedLastPage(int itemOnPage) {
        return postRepository.findAllByAllowedTrueOrderByIdDesc()
                .collectList()
                .flatMap(list -> Mono.just(getLastPage(list.size(), itemOnPage)));
    }

    public Flux<Post> findSearch(String search){
        return postRepository.findAllByContentContainsIgnoreCaseAndVerifiedIsTrue(search);
    }

    private int getLastPage(int size, int itemOnPage){
        int remains = size % itemOnPage;
        int last;
        if(remains != 0){
            last = size / itemOnPage;
        }else{
            last = (size / itemOnPage) - 1;
        }
        if(last < 0){
            last = 0;
        }
        return last;
    }

    public Mono<Post> verifyOff(int id) {
        return postRepository.findById(id)
                .flatMap(post -> {
                    post.setVerified(false);
                    return postRepository.save(post);
                });
    }

    public Mono<Object> allowOff(int id) {
        return postRepository.findById(id)
                .flatMap(post -> {
                    post.setAllowed(false);
                    return postRepository.save(post);
                });
    }

    public Mono<Post> findByIdAndVerifiedTrue(Integer id) {
        return postRepository.findByIdAndVerifiedTrue(id);
    }

    public Mono<Post> findByIdAndAllowedTrue(Integer id) {
        return postRepository.findByIdAndAllowedTrue(id);
    }

    public Mono<Post> findByIdAndVerifiedFalse(AppUser user, Integer id) {
        return postRepository.findByIdAndVerifiedFalse(id).flatMap(post -> {
            for(Role role : user.getRoles()){
                if(role.equals(Role.ADMIN)){
                    return Mono.just(post);
                }else if(role.equals(Role.MODERATOR)){
                    if(user.getSportTagIds().stream().anyMatch(tagId -> post.getSportTagIds().stream().anyMatch(postTagId -> postTagId.equals(tagId)))){
                        return Mono.just(post);
                    }else{
                        return Mono.empty();
                    }
                }else if(role.equals(Role.PUBLISHER)){
                    if(user.getPostIds().stream().anyMatch(postId -> postId.equals(id))){
                        return Mono.just(post);
                    }else{
                        return Mono.empty();
                    }
                }
            }
            return Mono.empty();
        });
    }

    public Mono<Boolean> userBookmark(AppUser user, int postId) {
        if(user != null){
            return postRepository.findById(postId).flatMap(post -> {
                if(post.getUserSaveList().stream().anyMatch(userId -> userId == user.getId())){
                    return Mono.just(true);
                }else{
                    return Mono.just(false);
                }
            });
        }else{
            return Mono.just(false);
        }
    }
}
