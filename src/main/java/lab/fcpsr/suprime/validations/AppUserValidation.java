package lab.fcpsr.suprime.validations;

import lab.fcpsr.suprime.dto.AppUserDTO;
import lab.fcpsr.suprime.services.AppReactiveUserDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.Period;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppUserValidation implements Validator {

    private final AppReactiveUserDetailService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(AppUserDTO.class);
    }

    @Override
    public void validate(@NotNull Object target, @NotNull Errors errors) {
        AppUserDTO userDTO = (AppUserDTO) target;
        checkPassword(userDTO, errors);
        checkAge(userDTO,errors);
        checkRoles(userDTO,errors);
    }

    private void checkPassword(AppUserDTO userDTO, Errors errors){
        if(!userDTO.getPassword().equals(userDTO.getConfirmPassword())){
            errors.rejectValue("confirmPassword", "", "Пароль не совпадает");
        }
    }

    private void checkAge(AppUserDTO userDTO, Errors errors){
        if(userDTO.getBirthday() != null) {
            LocalDate clientDate = userDTO.getBirthday();
            LocalDate currentDate = LocalDate.now();
            try {
                Period period = Period.between(clientDate, currentDate);
                if (period.getYears() < 18) {
                    errors.rejectValue("birthday", "", "Для регистрации вам должно быть больше 18 лет");
                }
            } catch (NullPointerException e) {
                errors.rejectValue("birthday", "", "Введите правильную дату");
            }
        }else{
            errors.rejectValue("birthday", "", "Вы не указали дату рождения");
        }
    }

    private void checkRoles(AppUserDTO userDTO, Errors errors){
        if(!userDTO.isAdmin() && !userDTO.isMainModerator() && !userDTO.isModerator() && !userDTO.isPublisher()){
            errors.rejectValue("admin","","Выберите хотя бы одну роль");
        }else{
            if(userDTO.isModerator()){
                if(userDTO.getModerTagIds() == null){
                    errors.rejectValue("admin","","Для роли Модератор не выбрано ни одно вида спорта");
                }
            }
        }
    }

    public Mono<Errors> checkUsername(AppUserDTO userDTO, Errors errors){
        if (!userDTO.getMail().equals("")) {
            return userService.findByEmail(userDTO.getMail()).map(user -> {
                if(user != null){
                    errors.rejectValue("mail", "", "Такой e-mail уже занят");
                }
                return errors;
            }).defaultIfEmpty(errors);
        }else{
            return Mono.just(errors);
        }
    }
}
