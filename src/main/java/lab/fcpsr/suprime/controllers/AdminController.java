package lab.fcpsr.suprime.controllers;

import jakarta.validation.Valid;
import lab.fcpsr.suprime.controllers.base.SuperController;
import lab.fcpsr.suprime.dto.SportTagDTO;
import lab.fcpsr.suprime.models.AppUser;
import lab.fcpsr.suprime.services.*;
import lab.fcpsr.suprime.validations.AppUserValidation;
import lab.fcpsr.suprime.validations.PostValidation;
import lab.fcpsr.suprime.validations.SliderValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminController extends SuperController {

    public AdminController(AppReactiveUserDetailService userService, MinioService minioService, MinioFileService fileService, SportTagService sportTagService, PostService postService, AppUserValidation userValidation, PostValidation postValidation, SliderValidation sliderValidation, RoleService roleService, SearchService searchService, SliderService sliderService, EventService eventService) {
        super(userService, minioService, fileService, sportTagService, postService, userValidation, postValidation, sliderValidation, roleService, searchService, sliderService, eventService);
    }

    @GetMapping
    @PreAuthorize("@RoleService.isAdmin(#user)")
    public Mono<Rendering> adminPage(@AuthenticationPrincipal AppUser user){
        return Mono.just(
                Rendering
                        .view("template")
                        .modelAttribute("index","admin-page")
                        .build()
        );
    }

    @GetMapping("/tag")
    @PreAuthorize("@RoleService.isAdmin(#user)")
    public Mono<Rendering> tagRegisterPage(@AuthenticationPrincipal AppUser user){
        return Mono.just(
                Rendering
                        .view("template")
                        .modelAttribute("index","tag-page")
                        .modelAttribute("sportTag", new SportTagDTO())
                        .build()
        );
    }

    @PostMapping("/tag")
    @PreAuthorize("@RoleService.isAdmin(#user)")
    public Mono<Rendering> registerNewTag(@AuthenticationPrincipal AppUser user, @ModelAttribute(name = "sportTag") @Valid SportTagDTO sportTag, Errors errors){
        if(errors.hasErrors()){
            return Mono.just(
                    Rendering
                            .view("template")
                            .modelAttribute("index","tag-page")
                            .modelAttribute("sportTag", sportTag)
                            .build()
            );
        }

        return sportTagService.save(sportTag).flatMap(st -> {
            log.info("saved: " + st.toString());
            return Mono.just(Rendering.redirectTo("/admin").build());
        });

        /*return minioService.uploadStream(sportTag.getFile())
                .flatMap(response -> {
                    log.info(response.toString());
                    return fileService.save(response);
                })
                .flatMap(file -> {
                    log.info(file.toString());
                    sportTag.setImageId(file.getId());
                    return sportTagService.save(sportTag);
                })
                .flatMap(st -> {
                    log.info(st.toString());
                    return Mono.just(Rendering.redirectTo("/admin").build());
                });*/
    }
}
