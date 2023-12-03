package lab.fcpsr.suprime.controllers;

import lab.fcpsr.suprime.controllers.base.SuperController;
import lab.fcpsr.suprime.services.AppReactiveUserDetailService;
import lab.fcpsr.suprime.services.MinioFileService;
import lab.fcpsr.suprime.services.MinioService;
import lab.fcpsr.suprime.services.SportTagService;
import lab.fcpsr.suprime.validations.AppUserValidation;
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
    public ContactController(AppReactiveUserDetailService userService, MinioService minioService, MinioFileService fileService, SportTagService sportTagService, AppUserValidation userValidation) {
        super(userService, minioService, fileService, sportTagService, userValidation);
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
