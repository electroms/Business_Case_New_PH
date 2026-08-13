package fr.humanbooster.businesscasespring.user;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    public DatabaseUserDetailsService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable : " + username));

        List<SimpleGrantedAuthority> authorities = Arrays.stream(normalizeRoles(appUser.getRoles()))
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

        return User.withUsername(appUser.getUsername())
            .password(appUser.getPassword())
            .authorities(authorities)
            .accountLocked(!appUser.isEnabled())
            .disabled(!appUser.isEnabled())
            .build();
    }

    private String[] normalizeRoles(String roles) {
        if (roles == null || roles.isBlank()) {
            return new String[] { "ROLE_USER" };
        }

        return Arrays.stream(roles.split(","))
            .map((String role) -> role.trim())
            .filter(role -> !role.isEmpty())
            .map((String role) -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
            .toArray(String[]::new);
    }
}
