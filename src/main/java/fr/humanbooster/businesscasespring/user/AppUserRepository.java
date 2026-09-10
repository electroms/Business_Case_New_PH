package fr.humanbooster.businesscasespring.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository JPA centralisant les accès aux utilisateurs.
 *
 * Il fournit les opérations CRUD standards ainsi que la recherche par nom
 * d'utilisateur utilisée par l'authentification Spring Security.
 */
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
}
