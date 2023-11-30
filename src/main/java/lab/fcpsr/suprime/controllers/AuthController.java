package lab.fcpsr.suprime.controllers;

import lab.fcpsr.suprime.dto.AppUserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    @GetMapping("/login")
    public Mono<Rendering> loginPage(){
        return Mono.just(Rendering
                .view("template")
                .modelAttribute("index","login-page")
                .build());
    }

    @GetMapping("/reg")
    public Mono<Rendering> registrationPage(){
        return Mono.just(Rendering
                .view("template")
                .modelAttribute("index","reg-page").modelAttribute("user", new AppUserDTO())
                .build()
        );
    }

    @PostMapping("/reg")
    public Mono<Rendering> registered(@ModelAttribute(name = "user") AppUserDTO userDTO){
        log.info(userDTO.toString());
        return Mono.just(Rendering.redirectTo("/").build());
    }
}
