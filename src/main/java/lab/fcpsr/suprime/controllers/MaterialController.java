package lab.fcpsr.suprime.controllers;

import jakarta.validation.Valid;
import lab.fcpsr.suprime.controllers.base.SuperController;
import lab.fcpsr.suprime.dto.PostDTO;
import lab.fcpsr.suprime.models.AppUser;
import lab.fcpsr.suprime.models.MinioFile;
import lab.fcpsr.suprime.models.Post;
import lab.fcpsr.suprime.services.*;
import lab.fcpsr.suprime.validations.AppUserValidation;
import lab.fcpsr.suprime.validations.PostValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequestMapping("/material")
public class MaterialController extends SuperController {

    private final int itemOnPage = 9;

    public MaterialController(AppReactiveUserDetailService userService, MinioService minioService, MinioFileService fileService, SportTagService sportTagService, PostService postService, AppUserValidation userValidation, PostValidation postValidation, RoleService roleService, SearchService searchService) {
        super(userService, minioService, fileService, sportTagService, postService, userValidation, postValidation, roleService, searchService);
    }

    @GetMapping
    @PreAuthorize("@RoleService.isAdmin(#user) || @RoleService.isModerator(#user) || @RoleService.isPublisher(#user)")
    public Mono<Rendering> materialPageMain(@AuthenticationPrincipal AppUser user){
        return Mono.just(Rendering.redirectTo("/material/page/0").build());
    }

    @GetMapping("/page/{num}")
    @PreAuthorize("@RoleService.isAdmin(#user) || @RoleService.isModerator(#user) || @RoleService.isPublisher(#user)")
    public Mono<Rendering> materialPage(@AuthenticationPrincipal AppUser user, @PathVariable int num){
        Flux<Post> postFlux = userService.findById(user.getId()).flatMapMany(u -> postService.findPostsByUserRole(u, PageRequest.of(num,itemOnPage)));
        Mono<Integer> lastPage = userService.findById(user.getId()).flatMap(u -> postService.findPostsByUserRoleGetLastPage(u,itemOnPage));
        return Mono.just(Rendering
                .view("template")
                .modelAttribute("index","material-page")
                .modelAttribute("posts", postFlux)
                .modelAttribute("page",num)
                .modelAttribute("lastPage",lastPage)
                .build());
    }

    @GetMapping("/search")
    @PreAuthorize("@RoleService.isAdmin(#user) || @RoleService.isModerator(#user) || @RoleService.isPublisher(#user)")
    public Mono<Rendering> searchResult(@AuthenticationPrincipal AppUser user, @RequestParam(name = "search") String request){
        return Mono.just(Rendering
                .view("template")
                .modelAttribute("posts",searchService.searchPosts(request).flatMap(id -> userService.findById(user.getId()).flatMap(u -> postService.findByIdAndVerifiedFalse(u,id))))
                .modelAttribute("index","material-page")
                .modelAttribute("page",0)
                .modelAttribute("lastPage", 0)
                .build());
    }

    @GetMapping("/post")
    @PreAuthorize("@RoleService.isPublisher(#user)")
    public Mono<Rendering> createPostPage(@AuthenticationPrincipal AppUser user){
        return Mono.just(Rendering
                .view("template")
                .modelAttribute("index","add-post-page")
                .modelAttribute("post", new PostDTO())
                .modelAttribute("sportTags", sportTagService.findAllToDTO())
                .modelAttribute("user",user)
                .build()
        );
    }

    @GetMapping("/off/verified/{id}")
    @PreAuthorize("@RoleService.checkModeration(#user,#id)")
    public Mono<Rendering> verifyOff(@AuthenticationPrincipal AppUser user, @PathVariable(name = "id") int id){
        return postService.verifyOff(id).flatMap(post -> Mono.just(Rendering.redirectTo("/").build()));
    }

    @PostMapping("/post")
    @PreAuthorize("@RoleService.isPublisher(#user)")
    public Mono<Rendering> addPost(@AuthenticationPrincipal AppUser user, @ModelAttribute(name = "post") @Valid PostDTO postDTO, Errors errors, @RequestPart(name = "sportTag",required = false) Flux<String> sportTags){
        postDTO.setUserId(user.getId());

        return sportTags.collectList()
                .flatMap(sportTagList -> {
                    postValidation.validate(postDTO,errors);
                    postValidation.checkTags(sportTagList,errors);
                    if(errors.hasErrors()){
                        return Mono.just(Rendering
                                .view("template")
                                .modelAttribute("index","add-post-page")
                                .modelAttribute("post", postDTO)
                                .modelAttribute("sportTags", sportTagService.findAllToDTO())
                                .modelAttribute("user",user)
                                .build());
                    }
                    for(String sportTag : sportTagList){
                        postDTO.addSportTagId(Integer.parseInt(sportTag));
                    }

                    return minioService.uploadStream(postDTO.getFile())
                            .flatMap(fileService::save)
                            .flatMap(file -> {
                                postDTO.setFileId(file.getId());
                                return minioService.uploadStream(postDTO.getImage());
                            })
                            .flatMap(fileService::save)
                            .flatMap(file -> {
                                postDTO.setImageId(file.getId());
                                return postService.save(postDTO);
                            })
                            .flatMap(post -> userService.setupPost(post)
                                    .flatMap(u -> fileService.setupPost(post))
                                    .flatMap(f -> sportTagService.setupPost(post).collectList())
                                    .flatMap(sl -> searchService.insertPost(post,sl))
                                    .flatMap(r -> {
                                        log.info("RESPONSE: " + r.toString());
                                        return Mono.just(Rendering.redirectTo("/material").build());
                                    })
                            );
                });
    }

    @GetMapping("/edit/post/{id}")
    @PreAuthorize("@RoleService.getAccess(#user,#id)")
    public Mono<Rendering> postEditPage(@AuthenticationPrincipal AppUser user, @PathVariable(name = "id") int id){
        return Mono.just(Rendering
                .view("template")
                .modelAttribute("index","edit-post-page")
                .modelAttribute("post",postService.findByIdDTO(id))
                .modelAttribute("sportTags", sportTagService.findAllByPostId(id))
                .modelAttribute("file", fileService.findByPostId(id))
                .modelAttribute("user",postService.findById(id).flatMap(post -> userService.findById(post.getUserId())))
                .build()
        );
    }

    @PostMapping("/edit/post/{id}")
    @PreAuthorize("@RoleService.getAccess(#user,#id)")
    public Mono<Rendering> updatePost(
            @AuthenticationPrincipal AppUser user,
            @PathVariable(name = "id") int id,
            @ModelAttribute(name = "post") @Valid PostDTO postDTO, Errors errors
    ){
        if(errors.hasErrors()){
            return Mono.just(Rendering
                    .view("template")
                    .modelAttribute("index","edit-post-page")
                    .modelAttribute("post", postDTO)
                    .modelAttribute("sportTags", sportTagService.findAllByPostId(id))
                    .modelAttribute("file", fileService.findByPostId(id))
                    .modelAttribute("user",user)
                    .build());
        }

        Mono<MinioFile> imageMono = null;
        if(postDTO.getImage() != null) {
            if (!postDTO.getImage().filename().equals("")) {
                imageMono = postService.findById(id)
                        .flatMap(post -> fileService.findById(post.getImageId())
                                .flatMap(file -> minioService.delete(file).then(Mono.just(file)))
                                .flatMap(file -> fileService.deleteById(file.getId()))
                                .flatMap(file -> minioService.uploadStream(postDTO.getImage()).flatMap(fileService::save)));
            }
        }
        Mono<MinioFile> fileMono = null;
        if(postDTO.getFile() != null) {
            if (!postDTO.getFile().filename().equals("")) {
                fileMono = postService.findById(id)
                        .flatMap(post -> fileService.findById(post.getFileId())
                                .flatMap(file -> minioService.delete(file).then(Mono.just(file)))
                                .flatMap(file -> fileService.deleteById(file.getId()))
                                .flatMap(file -> minioService.uploadStream(postDTO.getFile()).flatMap(fileService::save)));
            }
        }

        return postService.updatePost(id, postDTO, imageMono, fileMono)
                .flatMap(searchService::updatePost)
                .flatMap(response -> {
                    log.info(response.toString());
                    return Mono.just(Rendering.redirectTo("/material").build());
                });
    }

    @GetMapping("/post/verify/{id}")
    @PreAuthorize("@RoleService.isAdmin(#user) || @RoleService.isModerator(#user)")
    public Mono<Rendering> verifyPost(@AuthenticationPrincipal AppUser user, @PathVariable(name = "id") int id){
        return postService.findById(id)
                .flatMap(post -> {
                    post.setVerified(true);
                    return postService.save(post);
                })
                .flatMap(post -> Mono.just(Rendering.redirectTo("/").build()));
    }

    @GetMapping("/post/delete/{id}")
    @PreAuthorize("@RoleService.getAccess(#user,#id)")
    public Mono<Rendering> deletePost(@AuthenticationPrincipal AppUser user, @PathVariable(name = "id") int id){
        return postService.deletePost(id)
                .flatMap(userService::deletePostFromUser)
                .flatMap(sportTagService::deletePostFromSportTags)
                .flatMap(post -> fileService.deletePostData(post.getImageId()).flatMap(file -> minioService.delete(file).then(Mono.just(post))))
                .flatMap(post -> fileService.deletePostData(post.getFileId()).flatMap(file -> minioService.delete(file).then(Mono.just(post))))
                .flatMap(searchService::deletePost)
                .flatMap(deleteResponse -> {
                    log.info("DELETE: " + deleteResponse.toString());
                    return Mono.just(Rendering.redirectTo("/material").build());
                });
    }
}
