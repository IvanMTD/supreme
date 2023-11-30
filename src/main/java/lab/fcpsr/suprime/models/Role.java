package lab.fcpsr.suprime.models;

import java.util.Set;

public interface Role {
    boolean includes (Role role);

    static Set<Role> roots() {
        return Set.of(CategoryRoleType.ADMIN);
    }
}
