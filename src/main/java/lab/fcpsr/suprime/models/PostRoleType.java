package lab.fcpsr.suprime.models;

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
}
