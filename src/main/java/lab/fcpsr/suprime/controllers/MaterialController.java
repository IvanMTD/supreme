package lab.fcpsr.suprime.controllers;

import jakarta.validation.Valid;
import lab.fcpsr.suprime.controllers.base.SuperController;
import lab.fcpsr.suprime.dto.PostDTO;
import lab.fcpsr.suprime.models.AppUser;
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
                    return minioService.uploadStream(postDTO.getFile())
                            .flatMap(response -> fileService.save(response)
                                    .doOnNext(mf -> log.info("file saved in data_db witch id " + mf.getId()))
                                    .publishOn(Schedulers.boundedElastic())
                                    .flatMap(mf -> {
                                        postDTO.addFileId(mf.getId());
                                        Path filePath = CustomFileUtil.prepareFilePath(postDTO.getImage().filename());
                                        postDTO.setImagePath(filePath.toString());
                                        postDTO.getFile().transferTo(filePath).subscribe();
                                        log.info(postDTO.toString());
                                        return postService.save(postDTO)
                                                .publishOn(Schedulers.boundedElastic())
                                                .flatMap(post -> {
                                                    fileService.setupPost(post).subscribe();
                                                    sportTagService.setupPost(post).subscribe();
                                                    userService.setupPost(post).subscribe();
                                                    log.info("details: " + post);
                                                    return Mono.just(Rendering.redirectTo("/material").build());
                                                });
                                    })
                            );
                });
    }

    @ResponseBody
    @GetMapping("/src/main/resources/static/img/{fileName}")
    public byte[] getFile(@PathVariable String fileName){
        Path path = Path.of("src/main/resources/static/img/" + fileName);
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
