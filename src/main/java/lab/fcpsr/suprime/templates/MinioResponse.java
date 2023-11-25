package lab.fcpsr.suprime.templates;

import io.minio.ObjectWriteResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MinioResponse {
    private String originalFileName;
    private String uid;
    private String type;
    private int fileSize;
    private ObjectWriteResponse response;
}
