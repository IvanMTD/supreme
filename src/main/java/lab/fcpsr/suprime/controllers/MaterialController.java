package lab.fcpsr.suprime.controllers;

import jakarta.validation.Valid;
import lab.fcpsr.suprime.controllers.base.SuperController;
import lab.fcpsr.suprime.dto.PostDTO;
import lab.fcpsr.suprime.models.AppUser;
import lab.fcpsr.suprime.services.AppReactiveUserDetailService;
import lab.fcpsr.suprime.services.MinioFileService;
import lab.fcpsr.suprime.services.MinioService;
import lab.fcpsr.suprime.services.SportTagService;
import lab.fcpsr.suprime.validations.AppUserValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.Part;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/material")
public class MaterialController extends SuperController {

    public MaterialController(AppReactiveUserDetailService userService, MinioService minioService, MinioFileService fileService, SportTagService sportTagService, AppUserValidation userValidation) {
        super(userService, minioService, fileService, sportTagService, userValidation);
    }

    @GetMapping
    public Mono<Rendering> materialPage(){
        return Mono.just(Rendering
                .view("template")
                .modelAttribute("index","material-page")
                .build()
        );
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
        return sportTags.collectList().map(sportTagList -> {
            if(sportTagList.size() == 0){
                errors.rejectValue("sportTagIds","","Укажите хотя бы один вид спорта");
            }
            if(errors.hasErrors()){
                return Rendering
                        .view("template")
                        .modelAttribute("index","add-post-page")
                        .modelAttribute("post", postDTO)
                        .modelAttribute("sportTags", sportTagService.findAllToDTO())
                        .modelAttribute("user",user)
                        .build();
            }
            for(String sportTag : sportTagList){
                postDTO.addSportTagId(Integer.parseInt(sportTag));
            }
            log.info(postDTO.toString());
            return (Rendering.redirectTo("/").build());
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
