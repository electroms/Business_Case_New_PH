package fr.humanbooster.businesscasespring.api;

import fr.humanbooster.businesscasespring.user.AppUser;
import fr.humanbooster.businesscasespring.user.AppUserRepository;
import fr.humanbooster.businesscasespring.user.UserManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private final AppUserRepository appUserRepository;
    private final UserManagementService userManagementService;

    public UserController(AppUserRepository appUserRepository, UserManagementService userManagementService) {
        this.appUserRepository = appUserRepository;
        this.userManagementService = userManagementService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserSummaryDto>> listUsers() {
        List<UserSummaryDto> users = appUserRepository.findAll().stream()
            .map(UserSummaryDto::from)
            .toList();

        return ResponseEntity.ok(users);
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication != null ? authentication.getName() : "system";
            AppUser created = userManagementService.createUser(request.username(), request.password(), request.roles(), request.enabled(), currentUsername);
            return ResponseEntity.status(HttpStatus.CREATED).body(UserSummaryDto.from(created));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
        }
    }

    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@org.springframework.web.bind.annotation.PathVariable Long userId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication != null ? authentication.getName() : "system";
            userManagementService.deleteUser(userId, currentUsername);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
        }
    }

    public record UserSummaryDto(Long id, String username, String roles, boolean enabled) {
        public static UserSummaryDto from(AppUser user) {
            return new UserSummaryDto(user.getId(), user.getUsername(), user.getRoles(), user.isEnabled());
        }
    }

    public record ApiError(String message) {
    }
}
