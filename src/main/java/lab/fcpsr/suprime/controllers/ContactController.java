package lab.fcpsr.suprime.controllers;

import lab.fcpsr.suprime.controllers.base.SuperController;
import lab.fcpsr.suprime.services.*;
import lab.fcpsr.suprime.validations.AppUserValidation;
import lab.fcpsr.suprime.validations.PostValidation;
import lab.fcpsr.suprime.validations.SliderValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequestMapping("/contact")
public class ContactController extends SuperController {

    public ContactController(AppReactiveUserDetailService userService, MinioService minioService, MinioFileService fileService, SportTagService sportTagService, PostService postService, AppUserValidation userValidation, PostValidation postValidation, SliderValidation sliderValidation, RoleService roleService, SearchService searchService, SliderService sliderService) {
        super(userService, minioService, fileService, sportTagService, postService, userValidation, postValidation, sliderValidation, roleService, searchService, sliderService);
    }

    @GetMapping
    public Mono<Rendering> contactPage(){
        return Mono.just(Rendering
                .view("template")
                .modelAttribute("index","contact-page")
                .build()
        );
    }
}
