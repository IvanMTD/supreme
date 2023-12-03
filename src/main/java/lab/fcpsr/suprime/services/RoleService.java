package lab.fcpsr.suprime.services;

import lab.fcpsr.suprime.models.AppUser;
import lab.fcpsr.suprime.models.Role;
import org.springframework.stereotype.Service;

@Service("RoleService")
public class RoleService {
    public boolean isAdmin(AppUser user){
        if(user != null) {
            for (Role role : user.getRoles()) {
                if (role.equals(Role.ADMIN)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isPublisher(AppUser user){
        if(user != null){
            for(Role role : user.getRoles()){
                if(role.equals(Role.PUBLISHER)){
                    return true;
                }
            }
        }
        return false;
    }
}
