package fr.humanbooster.businesscasespring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée principal de l'application Spring Boot.
 *
 * Cette classe démarre le contexte Spring, initialise les composants de sécurité,
 * la couche JPA et les contrôleurs REST nécessaires au fonctionnement de l'API.
 */
@SpringBootApplication
public class BusinesscasespringApplication {

    /**
     * Lance l'application et démarre l'écosystème Spring Boot complet.
     */
    public static void main(String[] args) {
        SpringApplication.run(BusinesscasespringApplication.class, args);
    }

}
