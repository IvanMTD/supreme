package lab.fcpsr.suprime.controllers;

import lab.fcpsr.suprime.services.MinioFileService;
import lab.fcpsr.suprime.services.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MinioService minioService;
    private final MinioFileService fileService;

    @GetMapping("/")
    public Mono<Rendering> homePage(){
        return Mono.just(Rendering.view("home")
                .modelAttribute("title","Home")
                .modelAttribute("files",fileService.findAll())
                .build());
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

    @GetMapping("/download/{id}")
    public Mono<ResponseEntity<Mono<InputStreamResource>>> download(@PathVariable(name = "id") int id){
        return fileService.findById(id).map(fileInfo -> ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileInfo.getName())
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
}
