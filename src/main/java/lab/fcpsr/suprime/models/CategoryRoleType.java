package lab.fcpsr.suprime.models;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

public enum CategoryRoleType implements Role{
    ADMIN, MODERATOR;

    private final Set<Role> children = new HashSet<>();

    static {
        ADMIN.children.add(MODERATOR);
        MODERATOR.children.add(PostRoleType.MANAGER);
    }

    @Override
    public boolean includes(Role role) {
        return this.equals(role) || children.stream().anyMatch(r -> r.includes(role));
    }

    @Component("CategoryRole")
    @Getter
    static class SpringComponent {
        private final CategoryRoleType ADMIN = CategoryRoleType.ADMIN;
        private final CategoryRoleType MODERATOR = CategoryRoleType.MODERATOR;
    }
}
