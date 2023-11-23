package lab.fcpsr.suprime.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
public class HomeController {
    @GetMapping("/")
    public Mono<Rendering> homePage(){
        return Mono.just(Rendering.view("home").modelAttribute("title","Home").build());
    }
}
