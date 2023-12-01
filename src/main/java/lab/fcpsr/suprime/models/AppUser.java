package lab.fcpsr.suprime.models;

import javafx.geometry.Pos;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class AppUser implements UserDetails {
    @Id
    private int id;

    private Set<Integer> sportTagIds = new HashSet<>();
    private Set<Integer> postIds = new HashSet<>();
    private Set<Role> roles = new HashSet<>();

    private @NonNull String eMail;
    private @NonNull String password;
    private @NonNull String firstName;
    private @NonNull String middleName;
    private @NonNull String lastName;
    private @NonNull Date birthday;
    private @NonNull Date placedAt;
    private @NonNull String phone;

    public void addSportTag(SportTag sportTag){
        this.sportTagIds.add(sportTag.getId());
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
        return getEMail();
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
