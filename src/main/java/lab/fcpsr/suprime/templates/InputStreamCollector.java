package lab.fcpsr.suprime.templates;

import com.luciad.imageio.webp.WebPWriteParam;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;

import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;

@Slf4j
public class InputStreamCollector {
    ByteArrayOutputStream targetStream = new ByteArrayOutputStream();
    @SneakyThrows
    public InputStreamCollector collectInputStream(InputStream input) {
        IOUtils.copy(input, targetStream);
        return this;
    }

    public InputStream getStream() {
        return new ByteArrayInputStream(targetStream.toByteArray());
    }

    public void closeStream(){
        try {
            targetStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

/*
           minioService.download(fileInfo).flatMap(isr -> {
                    InputStreamResource inputStreamResource = isr;
                    try {
                        String temp = "./src/main/resources/static/img/temp.webp";
                        BufferedImage image = ImageIO.read(isr.getInputStream());
                        ImageIO.write(image,"webp", new File(temp));
                        File file = new File(temp);
                        InputStream inputStream = new FileInputStream(file);
                        inputStreamResource = new InputStreamResource(inputStream);
                    } catch (IOException e) {
                        System.out.println(e);
                    }
                    return Mono.just(inputStreamResource);
                })
 */
