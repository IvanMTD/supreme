package lab.fcpsr.suprime.controllers;

import io.minio.*;
import io.minio.errors.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Controller
public class HomeController {
    private final MinioClient minioClient;
    private final Path uploadDirectory = Path.of("./src/main/resources/temp");

    private final String bucket;

    @SneakyThrows
    public HomeController(MinioClient minioClient, String bucket) {
        this.bucket = bucket;
        this.minioClient = minioClient;
        if(!Files.exists(uploadDirectory)){
            Files.createDirectory(uploadDirectory);
        }
        if(!this.minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())){
            this.minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    @GetMapping("/")
    public Mono<Rendering> homePage(){
        return Mono.just(Rendering.view("home")
                .modelAttribute("title","Home")
                .build());
    }

    @SneakyThrows
    @PostMapping("/upload")
    public Mono<Rendering> upload(@RequestPart("file") Mono<FilePart> filePartMono){
        log.info("Начало метода upload");
        return filePartMono
                .subscribeOn(Schedulers.immediate())
                .flatMap(filePart -> filePart.transferTo(uploadDirectory.resolve(filePart.filename())).then(Mono.just(filePart.filename())))
                .map(fileName -> {
                    log.info("Временный файл создан - начинаю запись в файловое хранилище");
                    long startMillis = System.currentTimeMillis();
                    Path fullPath = uploadDirectory.resolve(fileName);
                    if(Files.exists(fullPath)) {
                        try {
                            FileInputStream inputStream = new FileInputStream(fullPath.toString());
                            long fileSize = Files.size(fullPath);
                            log.info("Размер файла: " + fileSize);
                            log.info("Расположение файла: " + fullPath);
                            PutObjectArgs putObjectArgs = PutObjectArgs
                                    .builder()
                                    .bucket(bucket)
                                    .object("video/" + fileName)
                                    .stream(inputStream, fileSize, -1)
                                    .contentType("video/mp4")
                                    .build();
                            ObjectWriteResponse response = minioClient.putObject(putObjectArgs);
                            log.info("Файл сохранен с тегом - " + response.etag());
                            log.info("Время загрузки {} ms", System.currentTimeMillis() - startMillis);
                            return Rendering.view("redirect:/").build();
                        } catch (Exception e) {
                            log.error(e.getMessage());
                            return Rendering.view("redirect:/").build();
                        }
                    }else{
                        log.info(fullPath + " | не существует");
                        return Rendering.view("redirect:/").build();
                    }
                })
                .log();
    }
}
