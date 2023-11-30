package lab.fcpsr.suprime.models;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

public enum PostRoleType implements Role {
    MANAGER, READER;

    private final Set<Role> children = new HashSet<>();

    static {
        MANAGER.children.add(READER);
    }

    @Override
    public boolean includes(Role role) {
        return this.equals(role) || children.stream().anyMatch(r -> r.includes(role));
    }

    @Component("PostRole")
    @Getter
    static class SpringComponent {
        private final PostRoleType MANAGER = PostRoleType.MANAGER;
        private final PostRoleType READER = PostRoleType.READER;
    }
}
