package lab.fcpsr.suprime.controllers;

import jakarta.validation.Valid;
import lab.fcpsr.suprime.controllers.base.SuperController;
import lab.fcpsr.suprime.dto.AppUserDTO;
import lab.fcpsr.suprime.models.AppUser;
import lab.fcpsr.suprime.models.Role;
import lab.fcpsr.suprime.services.*;
import lab.fcpsr.suprime.validations.AppUserValidation;
import lab.fcpsr.suprime.validations.PostValidation;
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

    public AuthController(AppReactiveUserDetailService userService, MinioService minioService, MinioFileService fileService, SportTagService sportTagService, PostService postService, AppUserValidation userValidation, PostValidation postValidation, RoleService roleService) {
        super(userService, minioService, fileService, sportTagService, postService, userValidation, postValidation, roleService);
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
        return Mono.just(Rendering
                .view("template")
                .modelAttribute("index","reg-page")
                .modelAttribute("sportTags", sportTagService.findAllToDTO())
                .modelAttribute("user", new AppUserDTO())
                .build()
        );
    }

    @PostMapping("/reg")
    @PreAuthorize("@RoleService.isAdmin(#user)")
    public Mono<Rendering> registered(@AuthenticationPrincipal AppUser user, @ModelAttribute(name = "user") @Valid AppUserDTO userDTO, Errors userErrors, ServerWebExchange exchange){
        return exchange.getFormData()
                .flatMap(form -> {
                    List<String> ids = form.get("sportTag");
                    if(ids == null){
                        ids = new ArrayList<>();
                    }
                    return Mono.just(ids);
                })
                .flatMap(ids -> userValidation.checkUsername(userDTO,userErrors)
                        .flatMap(userError -> {
                            List<Integer> tagIds = null;
                            if(ids.size() != 0){
                                tagIds = new ArrayList<>();
                                for(String id : ids){
                                    tagIds.add(Integer.valueOf(id));
                                }
                            }
                            userDTO.setModerTagIds(tagIds);
                            userValidation.validate(userDTO,userError);
                            if(userError.hasErrors()){
                                return Mono.just(Rendering
                                        .view("template")
                                        .modelAttribute("index","reg-page")
                                        .modelAttribute("sportTags", sportTagService.findAllToDTO())
                                        .modelAttribute("user", userDTO)
                                        .build());
                            }
                            return userService.save(userDTO).map(u -> Rendering.redirectTo("/").build());
                        })
                );
    }
}
