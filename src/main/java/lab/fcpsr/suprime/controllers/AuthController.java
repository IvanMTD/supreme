package lab.fcpsr.suprime.controllers;

import jakarta.validation.Valid;
import lab.fcpsr.suprime.controllers.base.SuperController;
import lab.fcpsr.suprime.dto.AppUserDTO;
import lab.fcpsr.suprime.models.AppUser;
import lab.fcpsr.suprime.models.Role;
import lab.fcpsr.suprime.services.*;
import lab.fcpsr.suprime.validations.AppUserValidation;
import lab.fcpsr.suprime.validations.PostValidation;
import lab.fcpsr.suprime.validations.SliderValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/auth")
public class AuthController extends SuperController {

    public AuthController(AppReactiveUserDetailService userService, MinioService minioService, MinioFileService fileService, SportTagService sportTagService, PostService postService, AppUserValidation userValidation, PostValidation postValidation, SliderValidation sliderValidation, RoleService roleService, SearchService searchService, SliderService sliderService, EventService eventService) {
        super(userService, minioService, fileService, sportTagService, postService, userValidation, postValidation, sliderValidation, roleService, searchService, sliderService, eventService);
    }

    @GetMapping("/login")
    public Mono<Rendering> loginPage(){
        return Mono.just(Rendering
                .view("template")
                .modelAttribute("index","login-page")
                .build());
    }

    @GetMapping("/reg")
    @PreAuthorize("@RoleService.isAdmin(#user)")
    public Mono<Rendering> registrationPage(@AuthenticationPrincipal AppUser user){
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("index","reg-page")
                        .modelAttribute("sportTags", sportTagService.findAllToDTO())
                        .modelAttribute("user", new AppUserDTO())
                        .build()
        );
    }

    @PostMapping("/reg")
    @PreAuthorize("@RoleService.isAdmin(#user)")
    public Mono<Rendering> registered(@AuthenticationPrincipal AppUser user, @ModelAttribute(name = "user") @Valid AppUserDTO userDTO, Errors userErrors, ServerWebExchange exchange){
        return exchange.getFormData().flatMap(form -> {
            List<String> sportTags = form.get("sportTag");
            if(sportTags == null){
                sportTags = new ArrayList<>();
            }
            return Mono.just(sportTags);
        }).flatMap(sportTags -> userValidation.checkUsername(userDTO, userErrors).flatMap(userError -> {
            List<Integer> tags = new ArrayList<>();
            for(String tag : sportTags){
                tags.add(Integer.valueOf(tag));
            }
            userDTO.setModerTagIds(tags);
            userValidation.validate(userDTO,userError);
            if(userError.hasErrors()){
                return Mono.just(Rendering
                        .view("template")
                        .modelAttribute("index","reg-page")
                        .modelAttribute("sportTags", sportTagService.findAllToDTO())
                        .modelAttribute("user", userDTO)
                        .build());
            }
            return userService.save(userDTO).flatMap(u -> sportTagService.addUserInTags(u).collectList()).flatMap(l -> Mono.just(Rendering.redirectTo("/").build()));
        }));
    }

    @GetMapping("/profile")
    @PreAuthorize("@RoleService.isAuthorize(#user)")
    public Mono<Rendering> profilePage(@AuthenticationPrincipal AppUser user){
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("index","profile-page")
                        .modelAttribute("user", userService.findById(user.getId()))
                        .modelAttribute("sportTags",userService.findById(user.getId()).flatMapMany(u -> sportTagService.findAllByIds(u.getSportTagIds())))
                        .modelAttribute("posts", userService.findById(user.getId()).flatMapMany(u -> postService.findAllByIds(u.getPostIds())))
                        .build()
        );
    }
}
