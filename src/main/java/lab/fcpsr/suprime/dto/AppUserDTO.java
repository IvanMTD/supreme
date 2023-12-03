package lab.fcpsr.suprime.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class AppUserDTO {
    private int id;
    @NotBlank(message = "Поле не может быть пустым")
    @Email(message = "Не валидный email")
    private String mail;
    @NotBlank(message = "Поле не может быть пустым")
    @Size(min = 8, message = "Пароль должен быть от 8 знаков")
    private String password;
    @NotBlank(message = "Поле не может быть пустым")
    private String confirmPassword;
    @NotBlank(message = "Поле не может быть пустым")
    @Size(min = 2, max = 10, message = "Имя может быть от 2 до 10 символов")
    @Pattern(regexp = "^[А-Я][а-я]+", message = "Имя должно быть в формате: 'Иван'")
    private String firstName;
    @Size(min = 2, max = 15, message = "Отчество может быть от 2 до 15 символов")
    @Pattern(regexp = "^[А-Я][а-я]+", message = "Отчество должно быть в формате: 'Иванович'")
    private String middleName;
    @NotBlank(message = "Поле не может быть пустым")
    @Size(min = 2, max = 20, message = "Фамилия может быть от 2 до 20 символов")
    @Pattern(regexp = "^[А-Я][а-я]+", message = "Фамилия должна быть в формате: 'Иванов'")
    private String lastName;
    @NotNull(message = "Укажите даду рождения")
    private LocalDate birthday;
    @NotBlank(message = "Укажите телефон")
    private String phone;

    private boolean admin;
    private boolean moderator;
    private boolean publisher;
    private List<Integer> moderTagIds;
}
