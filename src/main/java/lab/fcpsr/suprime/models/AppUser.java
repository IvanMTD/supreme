package lab.fcpsr.suprime.models;

import lab.fcpsr.suprime.dto.AppUserDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class AppUser implements UserDetails {
    @Id
    private int id;

    private Set<Integer> sportTagIds = new HashSet<>();
    private Set<Integer> postIds = new HashSet<>();
    private Set<Integer> postSaveList = new HashSet<>();
    private Set<Role> roles = new HashSet<>();

    private @NonNull String mail;
    private @NonNull String password;
    private @NonNull String firstName;
    private @NonNull String middleName;
    private @NonNull String lastName;
    private @NonNull LocalDate birthday;
    private @NonNull LocalDate placedAt;
    private @NonNull String phone;

    public AppUser(AppUserDTO verifiedUser){
        setMail(verifiedUser.getMail());
        setPassword(verifiedUser.getPassword());
        setFirstName(verifiedUser.getFirstName());
        setMiddleName(verifiedUser.getMiddleName());
        setLastName(verifiedUser.getLastName());
        setBirthday(verifiedUser.getBirthday());
        setPlacedAt(LocalDate.now());
        setPhone(verifiedUser.getPhone());

        if(verifiedUser.isAdmin()){
            roles.add(Role.ADMIN);
        }else if(verifiedUser.isMainModerator()){
            roles.add(Role.MAIN_MODERATOR);
        }else if(verifiedUser.isModerator()){
            roles.add(Role.MODERATOR);
            sportTagIds.addAll(verifiedUser.getModerTagIds());
        }else if(verifiedUser.isPublisher()){
            roles.add(Role.PUBLISHER);
        }else{
            roles.add(Role.READER);
        }
    }

    public String getPlacedAtAsString(){
        return placedAt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    public String getBirthdayAsString(){
        return birthday.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    public String getFullName(){
        return firstName + " " + middleName + " " + lastName;
    }

    public void addSportTag(SportTag sportTag){
        this.sportTagIds.add(sportTag.getId());
    }

    public void addPostInSaveList(Post post){
        postSaveList.add(post.getId());
    }

    public void addPost(Post post){
        this.postIds.add(post.getId());
    }

    public void addRole(Role role){
        this.roles.add(role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new HashSet<>();
    }

    @Override
    public String getUsername() {
        return getMail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
