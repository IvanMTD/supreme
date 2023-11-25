package lab.fcpsr.suprime.configurations;

import io.minio.MinioClient;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.TimeUnit;

@Configuration
@PropertySource("classpath:application.properties")
public class MinioConfiguration {
    @Value("${minio.url}")
    private String url;
    @Value("${minio.username}")
    private String username;
    @Value("${minio.password}")
    private String password;

    @Bean
    public MinioClient minioClient(){

        /*OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.MINUTES)
                .writeTimeout(10, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.MINUTES)
                .build();*/

        return MinioClient.builder()
                .endpoint(url)
                //.httpClient(httpClient)
                .credentials(username, password)
                .build();
    }
}
