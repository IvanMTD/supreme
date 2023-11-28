package lab.fcpsr.suprime.services;

import io.minio.*;
import lab.fcpsr.suprime.models.MinioFile;
import lab.fcpsr.suprime.templates.InputStreamCollector;
import lab.fcpsr.suprime.templates.MinioResponse;
import lab.fcpsr.suprime.utils.CustomFileUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.InputStream;
import java.util.Objects;

@Slf4j
@Service
@PropertySource("classpath:application.properties")
public class MinioService {
    private final String bucket;
    private final MinioClient minioClient;

    @SneakyThrows
    public MinioService(MinioClient minioClient, @Value("${minio.bucket}") String bucket) {
        this.bucket = bucket;
        log.info("bucket name is " + bucket);
        this.minioClient = minioClient;
        if(!this.minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())){
            this.minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    public Mono<MinioResponse> uploadStream(FilePart file){
        return file.content()
                .subscribeOn(Schedulers.boundedElastic())
                .reduce(new InputStreamCollector(),(collector, dataBuffer) -> collector.collectInputStream(dataBuffer.asInputStream()))
                .map(inputStreamCollector -> {
                    long startMillis = System.currentTimeMillis();
                    String extension = CustomFileUtil.getExtension(file.filename()).orElse("not");
                    String randomWord = "";
                    for(int i=0; i<10; i++){
                        char ch = (char) Math.round(65 + (Math.random() * 25.0f));
                        randomWord = randomWord + ch;
                    }
                    String uid = "uid-" + randomWord + "-." + extension;
                    try {
                        String type = Objects.requireNonNull(file.headers().getContentType()).toString();
                        PutObjectArgs args = PutObjectArgs.builder()
                                .bucket(bucket)
                                .object("/" + type + "/" + uid)
                                .contentType(type)
                                .stream(inputStreamCollector.getStream(), inputStreamCollector.getStream().available(), -1)
                                .build();
                        ObjectWriteResponse response = minioClient.putObject(args);
                        inputStreamCollector.closeStream();
                        MinioResponse minioResponse = new MinioResponse();
                        minioResponse.setResponse(response);
                        minioResponse.setOriginalFileName(file.filename());
                        minioResponse.setUid(uid);
                        minioResponse.setType(type);
                        minioResponse.setFileSize(CustomFileUtil.getMegaBytes(inputStreamCollector.getStream().available()));
                        log.info("upload stream execution time {} ms", System.currentTimeMillis() - startMillis);
                        return minioResponse;
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        throw new RuntimeException(e);
                    }
                })
                .log();
    }

    public Mono<InputStreamResource> download(MinioFile fileInfo){
        return Mono.fromCallable(() -> {
            InputStream response = minioClient.getObject(GetObjectArgs.builder().bucket(bucket).object(fileInfo.getPath()).build());
            return new InputStreamResource(response);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @SneakyThrows
    public Mono<Void> delete(MinioFile fileInfo) {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(fileInfo.getBucket())
                        .object(fileInfo.getPath())
                        .build()
        );
        return Mono.empty();
    }
}
