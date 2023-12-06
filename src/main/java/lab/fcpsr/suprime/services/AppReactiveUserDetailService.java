package lab.fcpsr.suprime.services;

import lab.fcpsr.suprime.dto.AppUserDTO;
import lab.fcpsr.suprime.models.AppUser;
import lab.fcpsr.suprime.models.Post;
import lab.fcpsr.suprime.repositories.AppUserRepository;
import lab.fcpsr.suprime.repositories.SportTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AppReactiveUserDetailService implements ReactiveUserDetailsService {
    private final AppUserRepository userRepository;
    private final SportTagRepository sportTagRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<AppUser> save(AppUserDTO userDTO) {
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        AppUser user = new AppUser(userDTO);
        return userRepository.save(user);
    }
    public Mono<AppUser> findByEmail(String mail){
        return userRepository.findByMail(mail);
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByMail(username).map(appUser -> appUser);
    }

    public Mono<AppUser> setupPost(Post post) {
        return userRepository.findById(post.getUserId())
                .flatMap(user -> {
                    user.addPost(post);
                    return sportTagRepository.findAllById(post.getSportTagIds())
                            .map(sportTag -> {
                                user.addSportTag(sportTag);
                                return user;
                            })
                            .last()
                            .flatMap(userRepository::save);
                });
    }
}
