package lab.fcpsr.suprime.controllers;

import lab.fcpsr.suprime.controllers.base.SuperController;
import lab.fcpsr.suprime.dto.TagPostDTO;
import lab.fcpsr.suprime.models.AppUser;
import lab.fcpsr.suprime.models.Post;
import lab.fcpsr.suprime.models.SportTag;
import lab.fcpsr.suprime.services.*;
import lab.fcpsr.suprime.validations.AppUserValidation;
import lab.fcpsr.suprime.validations.PostValidation;
import lab.fcpsr.suprime.validations.SliderValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Controller
public class HomeController extends SuperController {

    private final int itemOnPage = 5;
    private final int popularPostCount = 2;

    public HomeController(AppReactiveUserDetailService userService, MinioService minioService, MinioFileService fileService, SportTagService sportTagService, PostService postService, AppUserValidation userValidation, PostValidation postValidation, SliderValidation sliderValidation, RoleService roleService, SearchService searchService, SliderService sliderService, EventService eventService) {
        super(userService, minioService, fileService, sportTagService, postService, userValidation, postValidation, sliderValidation, roleService, searchService, sliderService, eventService);
    }


    @GetMapping("/")
    public Mono<Rendering> homePage(){
        //return Mono.just(Rendering.redirectTo("/page/0").build());
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("index","home-page")
                        .modelAttribute("page", 0)
                        .modelAttribute("lastPage", postService.findAllAllowedLastPage(itemOnPage))
                        .modelAttribute("posts", getTaggedPosts(0,itemOnPage))
                        .modelAttribute("popular", getTaggedPosts(0,popularPostCount))
                        .modelAttribute("sliders", sliderService.getAll())
                        .modelAttribute("events",eventService.getAllActual(PageRequest.of(0,4)))
                        .modelAttribute("statusSearch",false)
                        .build()
        );
    }

    /*@GetMapping("/page/{num}")
    public Mono<Rendering> pages(@PathVariable int num){
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("index","home-page")
                        .modelAttribute("page", num)
                        .modelAttribute("lastPage", postService.findAllAllowedLastPage(itemOnPage))
                        .modelAttribute("posts", getTaggedPosts(num,itemOnPage))
                        .modelAttribute("popular", getTaggedPosts(0,popularPostCount))
                        .modelAttribute("sliders", sliderService.getAll())
                        .modelAttribute("events",eventService.getAllActual(PageRequest.of(0,4)))
                        .modelAttribute("statusSearch",false)
                        .build()
        );
    }*/

    private Flux<TagPostDTO> getTaggedPosts(int page, int count){
        return postService.findAllAllowed(PageRequest.of(page,count)).flatMap(post -> {
            TagPostDTO cp = new TagPostDTO();
            cp.setPost(post);
            return sportTagService.findAll().collectList().flatMap(tags -> {
                for(SportTag tag : tags){
                    if(post.getSportTagIds().stream().anyMatch(tagId -> tagId == tag.getId())){
                        cp.addTag(tag);
                    }
                }
                return Mono.just(cp);
            });
        });
    }

    @GetMapping("/search")
    public Mono<Rendering> searchResult(@RequestParam(name = "search") String request){
        return Mono.just(Rendering
                .view("template")
                .modelAttribute("index","home-page")
                .modelAttribute("page",0)
                .modelAttribute("lastPage", 0)
                .modelAttribute("posts",getSearchedTaggedPosts(request))
                .modelAttribute("popular", getTaggedPosts(0,popularPostCount))
                .modelAttribute("sliders", sliderService.getAll())
                .modelAttribute("statusSearch",true)
                .build());
    }

    private Flux<TagPostDTO> getSearchedTaggedPosts(String request){
        return searchService.searchPosts(request).flatMap(postId -> postService.findByIdAndAllowedTrue(postId).flatMap(post -> {
            TagPostDTO cp = new TagPostDTO();
            cp.setPost(post);
            return sportTagService.findAll().collectList().flatMap(tags -> {
                for(SportTag tag : tags){
                    if(post.getSportTagIds().stream().anyMatch(tagId -> tagId == tag.getId())){
                        cp.addTag(tag);
                    }
                }
                return Mono.just(cp);
            });
        }));
    }

    @GetMapping("/bookmarks")
    @PreAuthorize("@RoleService.isAuthorize(#user)")
    public Mono<Rendering> myBookmarks(@AuthenticationPrincipal AppUser user){
        Flux<Post> postFlux = userService.findById(user.getId()).flatMapMany(u -> postService.findAllByIds(u.getPostSaveList()));
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("index", "bookmarks-page")
                        .modelAttribute("posts",postFlux)
                        .build()
        );
    }

    @GetMapping("/bookmark")
    @PreAuthorize("@RoleService.isAuthorize(#user)")
    public Mono<Rendering> bookmarkSave(@AuthenticationPrincipal AppUser user, @RequestParam(name = "bookmark") int postId){
        return postService.findById(postId).flatMap(post -> {
            post.addUserInSaveList(user);
            return postService.save(post);
        }).flatMap(post -> userService.findById(user.getId()).flatMap(u -> {
            u.addPostInSaveList(post);
            return userService.save(u);
        })).flatMap(u -> Mono.just(Rendering.redirectTo("/read/post/" + postId).build()));
    }

    @GetMapping("/bookmark/off")
    @PreAuthorize("@RoleService.isAuthorize(#user)")
    public Mono<Rendering> bookmarkOff(@AuthenticationPrincipal AppUser user, @RequestParam(name = "bookmark") int postId){
        return postService.findById(postId).flatMap(post -> {
            post.getUserSaveList().remove(user.getId());
            return postService.save(post);
        }).flatMap(post -> userService.findById(user.getId()).flatMap(u -> {
            u.getPostSaveList().remove(post.getId());
            return userService.save(u);
        })).flatMap(u -> Mono.just(Rendering.redirectTo("/read/post/" + postId).build()));
    }

    @GetMapping("/read/post/{id}")
    public Mono<Rendering> readPost(@AuthenticationPrincipal AppUser user, @PathVariable(name = "id") int id){
        return postService.findById(id)
                .flatMap(post -> Mono.just(
                        Rendering.view("template")
                                .modelAttribute("index","post-page")
                                .modelAttribute("post",post)
                                .modelAttribute("posts",postService.findAllAllowed(PageRequest.of(0,5)))
                                .modelAttribute("user",userService.findById(post.getUserId()))
                                .modelAttribute("sportTags",sportTagService.findAllByIds(post.getSportTagIds()))
                                .modelAttribute("file", fileService.findByPostId(post.getId()))
                                .modelAttribute("moderation", roleService.checkModeration(user,id))
                                .modelAttribute("bookmark", postService.userBookmark(user, id))
                                .build()
                ))
                .defaultIfEmpty(Rendering.redirectTo("/").build());
    }

    /*@SneakyThrows
    @PostMapping("/upload")
    public Mono<Rendering> upload(@RequestPart("file") FilePart file){
        log.info("incoming file " + file.filename());
        return minioService
                .uploadStream(file)
                .flatMap(response -> fileService.save(response)
                        .doOnNext(mf -> log.info("file saved in data_db witch id " + mf.getId()))
                        .map(mf -> Rendering.redirectTo("/").build())
                );
    }*/

    @ResponseBody
    @GetMapping("/download/{id}")
    public Mono<ResponseEntity<Mono<InputStreamResource>>> download(@PathVariable(name = "id") int id){
        return fileService.findById(id).map(fileInfo -> ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileInfo.getUid())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .body(minioService.download(fileInfo)));
    }

    /*@ResponseBody
    @GetMapping("/download/tag/{id}")
    public Mono<ResponseEntity<Mono<InputStreamResource>>> downloadTag(@PathVariable(name = "id") int id){
        return sportTagService.findById(id).flatMap(sportTag -> fileService.findById(sportTag.getImageId())).map(fileInfo -> ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileInfo.getUid())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .body(minioService.download(fileInfo)));
    }*/

    /*@ResponseStatus(HttpStatus.OK)
    @GetMapping("/delete/{id}")
    public Mono<Rendering> delete(@PathVariable(name = "id") int id){
        return fileService.deleteById(id)
                .publishOn(Schedulers.boundedElastic())
                .map(fileInfo -> {
                    minioService.delete(fileInfo).subscribe();
                    return Rendering.redirectTo("/").build();
                });
    }*/

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
