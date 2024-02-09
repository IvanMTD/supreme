package lab.fcpsr.suprime.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
public class Slider {
    @Id
    private int id;
    private String title;
    private String url;
    private int imageId;
}
