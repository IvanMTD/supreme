package lab.fcpsr.suprime.dto;

import jakarta.validation.constraints.NotBlank;
import lab.fcpsr.suprime.templates.Status;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class EventDTO {
    @NotBlank(message = "Не может быть пустым")
    private String content;
    @NotBlank(message = "Не может быть пустым")
    private String subject;
    @NotBlank(message = "Не может быть пустым")
    private String city;
    @NotBlank(message = "Не может быть пустым")
    private String location;
    private Status status;
    private int ekp;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate placedAt;
}
