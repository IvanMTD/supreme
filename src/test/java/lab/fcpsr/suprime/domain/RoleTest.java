package lab.fcpsr.suprime.domain;

import lab.fcpsr.suprime.models.CategoryRoleType;
import lab.fcpsr.suprime.models.PostRoleType;
import lab.fcpsr.suprime.models.Role;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@Slf4j
public class RoleTest {
    @Test
    void shouldNotThrowStackOverflowException() {
        final var roots = Role.roots();
        final var existingRoles = Stream.concat(
                stream(PostRoleType.values()),
                stream(CategoryRoleType.values())
        ).toList();

        assertDoesNotThrow(
                () -> {
                    for (Role root : roots) {
                        for (var roleToCheck : existingRoles) {
                            boolean check = root.includes(roleToCheck);
                        }
                    }
                }
        );
    }

    @ParameterizedTest
    @MethodSource("provideArgs")
    void shouldIncludeOrNotTheGivenRoles(Role root, Set<Role> rolesToCheck, boolean shouldInclude) {
        for (Role role : rolesToCheck) {
            assertEquals(
                    shouldInclude,
                    root.includes(role)
            );
        }
    }

    private static Stream<Arguments> provideArgs() {
        return Stream.of(
                arguments(
                        CategoryRoleType.ADMIN,
                        Stream.concat(
                                stream(PostRoleType.values()),
                                stream(CategoryRoleType.values())
                        ).collect(Collectors.toSet()),
                        true
                ),
                arguments(
                        CategoryRoleType.MODERATOR,
                        Set.of(PostRoleType.MANAGER, PostRoleType.READER, CategoryRoleType.MODERATOR),
                        true
                ),
                arguments(
                        PostRoleType.READER,
                        Set.of(PostRoleType.MANAGER),
                        false
                ),
                arguments(
                        CategoryRoleType.MODERATOR,
                        Set.of(CategoryRoleType.ADMIN),
                        false
                )
        );
    }
}
