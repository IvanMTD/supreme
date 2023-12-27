package lab.fcpsr.suprime.controllers.base;

import lab.fcpsr.suprime.models.AppUser;
import lab.fcpsr.suprime.models.Role;
import lab.fcpsr.suprime.services.*;
import lab.fcpsr.suprime.validations.AppUserValidation;
import lab.fcpsr.suprime.validations.PostValidation;
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
    protected final PostService postService;
    protected final AppUserValidation userValidation;
    protected final PostValidation postValidation;

    protected final RoleService roleService;

    protected final SearchService searchService;

    @ModelAttribute(name = "baseAuth")
    public boolean baseAuth(@AuthenticationPrincipal AppUser user){
        return user != null;
    }

    @ModelAttribute(name = "roleAdmin")
    public boolean roleAdmin(@AuthenticationPrincipal AppUser user){
        return roleService.isAdmin(user);
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
        return "Supreme Project";
    }
}
