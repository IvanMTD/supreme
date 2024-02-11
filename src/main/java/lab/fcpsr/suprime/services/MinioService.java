package lab.fcpsr.suprime.services;

import io.minio.*;
import io.minio.errors.*;
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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Slf4j
@Service
@PropertySource("classpath:application.properties")
public class MinioService {
    private final String bucket;
    private final MinioClient minioClient;

    @SneakyThrows
    public MinioService(MinioClient minioClient, @Value("${minio.bucket}") String bucket) {
        this.minioClient = minioClient;
        this.bucket = bucket;
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
                    String uid = "uid-" + randomWord + "." + extension;
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
    public Mono<MinioResponse> uploadImage(FilePart image){
        String directory = "./src/main/resources/static/img/";
        return image.transferTo(Path.of(directory + "temp")).then(
                Mono.just(new File(directory + "temp")).flatMap(file -> {
                    if(file.exists()){
                        String extension = "webp";
                        String randomWord = "";
                        for(int i=0; i<10; i++){
                            char ch = (char) Math.round(65 + (Math.random() * 25.0f));
                            randomWord = randomWord + ch;
                        }
                        String uid = "uid-" + randomWord + "." + extension;
                        log.info("try save " + uid);
                        try {
                            String temp = directory + "temp.webp";
                            InputStream fileInputStream = new FileInputStream(file);
                            BufferedImage bufferedImage = ImageIO.read(fileInputStream);
                            log.info(bufferedImage.toString());
                            if(ImageIO.write(bufferedImage, "webp", new File(temp))) {
                                File webpImage = new File(temp);
                                InputStream webpImageInputStream = new FileInputStream(webpImage);
                                String type = "image/webp";
                                PutObjectArgs args = PutObjectArgs.builder()
                                        .bucket(bucket)
                                        .object("/" + type + "/" + uid)
                                        .contentType(type)
                                        .stream(webpImageInputStream, -1, 10485760)
                                        .build();
                                ObjectWriteResponse response = minioClient.putObject(args);
                                MinioResponse minioResponse = new MinioResponse();
                                minioResponse.setResponse(response);
                                minioResponse.setOriginalFileName(image.filename());
                                minioResponse.setUid(uid);
                                minioResponse.setType(type);
                                minioResponse.setFileSize(CustomFileUtil.getMegaBytes(fileInputStream.available()));
                                fileInputStream.close();
                                webpImageInputStream.close();
                                cleanup();
                                return Mono.just(minioResponse);
                            }else{
                                cleanup();
                                return uploadStream(image);
                            }
                        } catch (ServerException | InsufficientDataException | ErrorResponseException |
                                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException |
                                 XmlParserException | InternalException | IOException e) {
                            return Mono.error(new RuntimeException(e));
                        }
                    }else{
                        log.info("file in " + directory + "temp not exist");
                    }
                    return Mono.empty();
                })
        );
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

    @SneakyThrows
    private void cleanup(){
        String directory = "./src/main/resources/static/img/";
        File f1 = new File(directory + "temp");
        File f2 = new File(directory + "temp.webp");
        if(f1.exists()){
            Files.delete(f1.toPath());
        }
        if(f2.exists()){
            Files.delete(f2.toPath());
        }
    }
}
