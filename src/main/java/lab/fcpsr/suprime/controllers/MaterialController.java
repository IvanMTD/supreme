package lab.fcpsr.suprime.controllers;

import jakarta.validation.Valid;
import lab.fcpsr.suprime.controllers.base.SuperController;
import lab.fcpsr.suprime.dto.PostDTO;
import lab.fcpsr.suprime.models.AppUser;
import lab.fcpsr.suprime.models.MinioFile;
import lab.fcpsr.suprime.models.SportTag;
import lab.fcpsr.suprime.services.*;
import lab.fcpsr.suprime.utils.CustomFileUtil;
import lab.fcpsr.suprime.validations.AppUserValidation;
import lab.fcpsr.suprime.validations.PostValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Controller
@RequestMapping("/material")
public class MaterialController extends SuperController {

    public MaterialController(AppReactiveUserDetailService userService, MinioService minioService, MinioFileService fileService, SportTagService sportTagService, PostService postService, AppUserValidation userValidation, PostValidation postValidation, RoleService roleService) {
        super(userService, minioService, fileService, sportTagService, postService, userValidation, postValidation, roleService);
    }

    @GetMapping
    @PreAuthorize("@RoleService.isAdmin(#user) || @RoleService.isModerator(#user) || @RoleService.isPublisher(#user)")
    public Mono<Rendering> materialPage(@AuthenticationPrincipal AppUser user){
        return roleService.getMaterialsByRole(user);
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

    @PostMapping("/post")
    @PreAuthorize("@RoleService.isPublisher(#user)")
    public Mono<Rendering> createPost(@AuthenticationPrincipal AppUser user, @ModelAttribute(name = "post") @Valid PostDTO postDTO, Errors errors, @RequestPart(name = "sportTag",required = false) Flux<String> sportTags){
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

                    Path filePath = CustomFileUtil.prepareFilePath(postDTO.getImage().filename());
                    postDTO.setImagePath(filePath.toString());
                    postDTO.getFile().transferTo(filePath).subscribe();

                    return minioService.uploadStream(postDTO.getFile())
                            .flatMap(response -> fileService.save(response)
                                    .doOnNext(mf -> log.info("file saved in data_db witch id " + mf.getId()))
                                    .flatMap(mf -> {
                                        postDTO.addFileId(mf.getId());
                                        return postService.save(postDTO)
                                                .flatMap(post -> userService.setupPost(post)
                                                        .doOnNext(u -> log.info("post added: " + u.toString()))
                                                        .flatMap(u -> {
                                                            return sportTagService.setupPost(post).collectList();
                                                        })
                                                        .doOnNext(l -> {
                                                            for(SportTag s : l){
                                                                log.info("sport tag saved " + s.toString());
                                                            }
                                                        })
                                                        .flatMap(l -> {
                                                            return fileService.setupPost(post).collectList();
                                                        })
                                                        .doOnNext(f -> {
                                                            for(MinioFile file : f){
                                                                log.info("file saved in db " + file.toString());
                                                            }
                                                        })
                                                        .flatMap(f -> Mono.just(Rendering.redirectTo("/material").build()))
                                                );
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
                .modelAttribute("user",user)
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
        log.info(postDTO.toString());
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
        return postService.updatePost(postDTO, id)
                .flatMap(post -> {
                    log.info("updated: " + post.toString());
                    return Mono.just(Rendering.redirectTo("/material").build());
                });
    }

    @GetMapping("/post/delete/{id}")
    @PreAuthorize("@RoleService.getAccess(#user,#id)")
    public Mono<Rendering> deletePost(@AuthenticationPrincipal AppUser user, @PathVariable(name = "id") int id){
        return postService.deletePost(id)
                .flatMap(userService::deletePostFromUser)
                .flatMap(sportTagService::deletePostFromSportTags)
                .publishOn(Schedulers.boundedElastic())
                .flatMap(post -> {
                    log.info(post.toString());
                    try {
                        Files.delete(Path.of(post.getImagePath()));
                    } catch (IOException e) {
                        return Mono.error(new RuntimeException(e));
                    }
                    return fileService.deleteFileByPost(post);
                })
                .flatMap(minioFile -> {
                    log.info("In delete method: " + minioFile);
                    return minioService.delete(minioFile)
                        .then(Mono.just(Rendering.redirectTo("/material").build()));
                });
    }
}
