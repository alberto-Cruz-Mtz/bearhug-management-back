package bearhug.management.app.service.implementation;

import bearhug.management.app.persistence.entity.RoleEntity;
import bearhug.management.app.persistence.entity.UserEntity;
import bearhug.management.app.persistence.model.RoleType;
import bearhug.management.app.persistence.repository.UserRepository;
import bearhug.management.app.presentation.dto.AuthRequest;
import bearhug.management.app.presentation.dto.AuthResponse;
import bearhug.management.app.service.interfaces.IUserService;
import bearhug.management.app.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class UserDetailServiceImpl implements UserDetailsService, IUserService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        List<SimpleGrantedAuthority> grantedAuthorities = this.getGrantedAuthorities(user);

        return new User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isAccountNonLocked(),
                grantedAuthorities
        );
    }

    @Override
    public AuthResponse register(AuthRequest authRequest) {
        String username = authRequest.username();
        String password = authRequest.password();

        UserEntity user = UserEntity.builder()
                .username(username)
                .password(password)
                .roles(Set.of(new RoleEntity(RoleType.USER)))
                .build();

        UserEntity savedUser = userRepository.save(user);

        List<SimpleGrantedAuthority> grantedAuthorities = this.getGrantedAuthorities(savedUser);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);
        String token = jwtUtil.generateToken(authentication);
        return new AuthResponse("User created successfully", token, true);
    }

    @Override
    public AuthResponse authenticate(AuthRequest authRequest) {
        String username = authRequest.username();
        String password = authRequest.password();

        Authentication authentication = this.verifyUser(authRequest.username(), password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtil.generateToken(authentication);
        return new AuthResponse("User authenticated successfully", token, true);
    }

    private Authentication verifyUser(String username, String password) {
        UserDetails userDetails = this.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, userDetails.getPassword()))
            throw new BadCredentialsException("Invalid password");
        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }

    private List<SimpleGrantedAuthority> getGrantedAuthorities(UserEntity user) {
        List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
        user.getRoles().forEach(role -> grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleType().name()))));
        user.getRoles().stream().map(RoleEntity::getPermissions).flatMap(Collection::stream).forEach(permission -> grantedAuthorities.add(new SimpleGrantedAuthority(permission.getPermissionType().name())));
        return grantedAuthorities;
    }
}
