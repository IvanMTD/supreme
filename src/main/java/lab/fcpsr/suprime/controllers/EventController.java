package lab.fcpsr.suprime.controllers;

import lab.fcpsr.suprime.controllers.base.SuperController;
import lab.fcpsr.suprime.services.*;
import lab.fcpsr.suprime.validations.AppUserValidation;
import lab.fcpsr.suprime.validations.PostValidation;
import lab.fcpsr.suprime.validations.SliderValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/event")
public class EventController extends SuperController {

    public EventController(AppReactiveUserDetailService userService, MinioService minioService, MinioFileService fileService, SportTagService sportTagService, PostService postService, AppUserValidation userValidation, PostValidation postValidation, SliderValidation sliderValidation, RoleService roleService, SearchService searchService, SliderService sliderService, EventService eventService) {
        super(userService, minioService, fileService, sportTagService, postService, userValidation, postValidation, sliderValidation, roleService, searchService, sliderService, eventService);
    }

    @GetMapping("/card/{id}")
    public Mono<Rendering> eventCardPage(@PathVariable(name = "id") int id){
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("index","event-card")
                        .modelAttribute("event", eventService.getEventById(id))
                        .build()
        );
    }
}
