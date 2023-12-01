package lab.fcpsr.suprime.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class AppUserDTO {
    private @NonNull String eMail;
    private @NonNull String password;
    private @NonNull String confirmPassword;
    private @NonNull String firstName;
    private @NonNull String middleName;
    private @NonNull String lastName;
    private @DateTimeFormat(pattern = "dd-MM-yyyy") @NonNull Date birthday;
    private @NonNull String phone;
}
