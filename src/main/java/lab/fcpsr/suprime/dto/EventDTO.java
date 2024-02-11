package lab.fcpsr.suprime.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class EventDTO {
    @NotBlank(message = "Не может быть пустым")
    private String title;
    @NotBlank(message = "Не может быть пустым")
    private String description;
    @NotBlank(message = "Не может быть пустым")
    private String eventUrl;
    private LocalDate eventDate;
}
