package fr.humanbooster.businesscasespring.api;

import fr.humanbooster.businesscasespring.user.AppUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur exposant les données de tableau de bord à l'application front.
 *
 * Le front utilise cette API pour afficher un résumé de l'utilisateur connecté,
 * le nom de l'application et des informations globales comme le nombre total d'utilisateurs.
 */
@RestController
@RequestMapping("/api")
public class DashboardController {

    private final AppUserRepository appUserRepository;

    public DashboardController(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDto> dashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication != null ? authentication.getName() : "anonymous";
        long totalUsers = appUserRepository.count();

        return ResponseEntity.ok(new DashboardDto(
            username,
            "Business Case",
            totalUsers,
            authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))
        ));
    }

    public record DashboardDto(String username, String applicationName, long totalUsers, boolean isAdmin) {
    }
}
