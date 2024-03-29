package lab.fcpsr.suprime.controllers.base;

import lab.fcpsr.suprime.models.AppUser;
import lab.fcpsr.suprime.services.*;
import lab.fcpsr.suprime.validations.AppUserValidation;
import lab.fcpsr.suprime.validations.PostValidation;
import lab.fcpsr.suprime.validations.SliderValidation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

@Slf4j
@Data
@Controller
@RequiredArgsConstructor
public class SuperController {

    protected final AppReactiveUserDetailService userService;
    protected final MinioService minioService;
    protected final MinioFileService fileService;
    protected final SportTagService sportTagService;
    protected final PostService postService;
    protected final AppUserValidation userValidation;
    protected final PostValidation postValidation;

    protected final SliderValidation sliderValidation;

    protected final RoleService roleService;

    protected final SearchService searchService;

    protected final SliderService sliderService;

    protected final EventService eventService;

    @ModelAttribute(name = "baseAuth")
    public boolean baseAuth(@AuthenticationPrincipal AppUser user){
        return user != null;
    }

    @ModelAttribute(name = "roleAdmin")
    public boolean roleAdmin(@AuthenticationPrincipal AppUser user){
        return roleService.isAdmin(user);
    }

    @ModelAttribute(name = "roleMainModerator")
    public boolean roleMainModerator(@AuthenticationPrincipal AppUser user){
        return roleService.isMainModerator(user);
    }
    @ModelAttribute(name = "roleModerator")
    public boolean roleModerator(@AuthenticationPrincipal AppUser user){
        return roleService.isModerator(user);
    }

    @ModelAttribute(name = "rolePublisher")
    public boolean rolePublisher(@AuthenticationPrincipal AppUser user){
        return roleService.isPublisher(user);
    }

    @ModelAttribute(name = "title")
    public String title(){
        return "Домен Спорт";
    }
}
