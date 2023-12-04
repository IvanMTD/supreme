package lab.fcpsr.suprime.controllers.base;

import lab.fcpsr.suprime.models.AppUser;
import lab.fcpsr.suprime.models.Role;
import lab.fcpsr.suprime.services.AppReactiveUserDetailService;
import lab.fcpsr.suprime.services.MinioFileService;
import lab.fcpsr.suprime.services.MinioService;
import lab.fcpsr.suprime.services.SportTagService;
import lab.fcpsr.suprime.validations.AppUserValidation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Data
@Controller
@RequiredArgsConstructor
public class SuperController {

    protected final AppReactiveUserDetailService userService;
    protected final MinioService minioService;
    protected final MinioFileService fileService;
    protected final SportTagService sportTagService;
    protected final AppUserValidation userValidation;

    @ModelAttribute(name = "baseAuth")
    public boolean baseAuth(@AuthenticationPrincipal AppUser user){
        return user != null;
    }

    @ModelAttribute(name = "roleAdmin")
    public boolean roleAdmin(@AuthenticationPrincipal AppUser user){
        if(user != null) {
            for (Role role : user.getRoles()) {
                if(role.equals(Role.ADMIN)){
                    return true;
                }
            }
        }
        return false;
    }

    @ModelAttribute(name = "rolePublisher")
    public boolean rolePublisher(@AuthenticationPrincipal AppUser user){
        if(user != null) {
            for (Role role : user.getRoles()) {
                if(role.equals(Role.PUBLISHER)){
                    return true;
                }
            }
        }
        return false;
    }

    @ModelAttribute(name = "title")
    public String title(){
        return "Supreme Project";
    }
}
