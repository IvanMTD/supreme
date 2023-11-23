package lab.fcpsr.suprime.controllers;

import io.minio.*;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MinioClient minioClient;

    @GetMapping("/")
    public Mono<Rendering> homePage(){
        return Mono.just(Rendering.view("home")
                .modelAttribute("title","Home")
                .build());
    }

    @PostMapping("/upload")
    public Mono<Rendering> upload(@RequestPart("file") Mono<FilePart> filePartMono){
        return filePartMono.publishOn(Schedulers.boundedElastic()).map(fp -> {
            try {
                if(!minioClient.bucketExists(BucketExistsArgs.builder().bucket("upload").build())){
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket("upload").build());
                }
                InputStream initialStream = new FileInputStream(fp.filename());
                File file = new File("./src/main/resources/temp/" + fp.filename());
                Files.copy(initialStream,file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                if(file.exists()){
                    minioClient
                            .putObject(
                                    PutObjectArgs.builder()
                                            .bucket("morgan")
                                            .object(fp.filename())
                                            .stream(new FileInputStream(file.getAbsolutePath()),file.getTotalSpace(),fp.headers().getContentLength())
                                            .build()
                            );
                }
                IOUtils.closeQuietly(initialStream);
            } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                     InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                     XmlParserException e) {
                throw new RuntimeException(e);
            }
            return Rendering.view("redirect:/").build();
        });
    }
}
