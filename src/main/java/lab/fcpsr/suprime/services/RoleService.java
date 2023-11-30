package lab.fcpsr.suprime.services;

import lab.fcpsr.suprime.models.*;
import lab.fcpsr.suprime.repositories.CategoryRoleRepository;
import lab.fcpsr.suprime.repositories.PostRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service("RoleService")
@RequiredArgsConstructor
public class RoleService {
    private final CategoryRoleRepository categoryRoleRepository;
    private final PostRoleRepository postRoleRepository;

    @Transactional
    public boolean hasRoleByCategoryId(Integer categoryId, Role role) {
        AppUser user = (AppUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<CategoryRole> categoryRoleOptional = categoryRoleRepository.findCategoryRoleByUserIdAndCategoryId(user.getId(), categoryId).blockOptional();
        if(categoryRoleOptional.isPresent()) {
            CategoryRole categoryRole = categoryRoleOptional.get();
            if (categoryRole.getType().includes(role)) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public boolean hasRoleByPostId(Integer postId, Role role) {
        AppUser user = (AppUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<PostRole> postRoleOptional = postRoleRepository.findPostRoleByUserIdAndPostId(user.getId(), postId).blockOptional();
        if(postRoleOptional.isPresent()) {
            PostRole postRole = postRoleOptional.get();
            if (postRole.getType().includes(role)) {
                return true;
            }
        }
        return false;
    }
}
