package lab.fcpsr.suprime.controllers;

import lab.fcpsr.suprime.controllers.base.SuperController;
import lab.fcpsr.suprime.models.AppUser;
import lab.fcpsr.suprime.services.*;
import lab.fcpsr.suprime.validations.AppUserValidation;
import lab.fcpsr.suprime.validations.PostValidation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Controller
public class HomeController extends SuperController {

    private final int itemOnPage = 4;

    public HomeController(AppReactiveUserDetailService userService, MinioService minioService, MinioFileService fileService, SportTagService sportTagService, PostService postService, AppUserValidation userValidation, PostValidation postValidation, RoleService roleService, SearchService searchService) {
        super(userService, minioService, fileService, sportTagService, postService, userValidation, postValidation, roleService, searchService);
    }

    @GetMapping("/")
    public Mono<Rendering> homePage(){
        return Mono.just(Rendering.redirectTo("/page/0").build());
    }

    @GetMapping("/page/{num}")
    public Mono<Rendering> pages(@PathVariable int num){
        return Mono.just(Rendering
                .view("template")
                .modelAttribute("posts",postService.findAllVerified(PageRequest.of(num,itemOnPage)))
                .modelAttribute("index","home-page")
                .modelAttribute("page",num)
                .modelAttribute("lastPage", postService.findAllVerifiedLastPage(itemOnPage))
                .build());
    }

    @GetMapping("/search")
    public Mono<Rendering> searchResult(@RequestParam(name = "search") String request){
        return Mono.just(Rendering
                .view("template")
                .modelAttribute("posts",searchService.searchPosts(request).flatMap(postService::findByIdAndVerifiedTrue))
                .modelAttribute("index","home-page")
                .modelAttribute("page",0)
                .modelAttribute("lastPage", 0)
                .build());
    }

    @GetMapping("/read/post/{id}")
    public Mono<Rendering> readPost(@AuthenticationPrincipal AppUser user, @PathVariable(name = "id") int id){
        return postService.findById(id)
                .flatMap(post -> Mono.just(Rendering
                        .view("template")
                        .modelAttribute("index","post-page")
                        .modelAttribute("post",post)
                        .modelAttribute("user",userService.findById(post.getUserId()))
                        .modelAttribute("sportTags",sportTagService.findAllByIds(post.getSportTagIds()))
                        .modelAttribute("file", fileService.findByPostId(post.getId()))
                        .modelAttribute("moderation", roleService.checkModeration(user,id))
                        .build()
                ))
                .defaultIfEmpty(Rendering.redirectTo("/").build());
    }

    @SneakyThrows
    @PostMapping("/upload")
    public Mono<Rendering> upload(@RequestPart("file") FilePart file){
        log.info("incoming file " + file.filename());
        return minioService
                .uploadStream(file).flatMap(response -> fileService.save(response)
                        .doOnNext(mf -> log.info("file saved in data_db witch id " + mf.getId()))
                        .map(mf -> Rendering.redirectTo("/").build())
                );
    }

    @ResponseBody
    @GetMapping("/download/{id}")
    public Mono<ResponseEntity<Mono<InputStreamResource>>> download(@PathVariable(name = "id") int id){
        return fileService.findById(id).map(fileInfo -> ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileInfo.getUid())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .body(minioService.download(fileInfo)));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/delete/{id}")
    public Mono<Rendering> delete(@PathVariable(name = "id") int id){
        return fileService.deleteById(id)
                .publishOn(Schedulers.boundedElastic())
                .map(fileInfo -> {
                    minioService.delete(fileInfo).subscribe();
                    return Rendering.redirectTo("/").build();
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
