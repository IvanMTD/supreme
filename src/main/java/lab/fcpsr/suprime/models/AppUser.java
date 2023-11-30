package lab.fcpsr.suprime.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class AppUser implements UserDetails {
    @Id
    private int id;

    private Set<Integer> categoryRoleIds = new HashSet<>();
    private Set<Integer> postRoleIds = new HashSet<>();

    private @NonNull String eMail;
    private @NonNull String password;
    private @NonNull String firstName;
    private @NonNull String middleName;
    private @NonNull String lastName;
    private @NonNull Date birthday;
    private @NonNull Date placedAt;
    private @NonNull String phone;

    public void addCategoryRole(CategoryRole categoryRole){
        categoryRoleIds.add(categoryRole.getId());
    }

    public void addPostRole(PostRole postRole){
        postRoleIds.add(postRole.getId());
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
