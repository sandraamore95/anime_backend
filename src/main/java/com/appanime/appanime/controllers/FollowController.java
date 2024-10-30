package com.appanime.appanime.controllers;
import com.appanime.appanime.models.Notification;
import com.appanime.appanime.models.User;
import com.appanime.appanime.security.services.UserService;
import com.appanime.appanime.services.FollowService;
import com.appanime.appanime.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/follows")
public class FollowController {

    @Autowired
    private FollowService followService;

    @Autowired
    private UserService userService;

    @Autowired
    NotificationService notificationService;

    // Seguir a un usuario
    @PostMapping("/follow/{userId}")
    public ResponseEntity<String> followUser(@PathVariable Long userId, Principal principal) {
        try {
            User follower = userService.getUserByUsername(principal.getName());
            User followed = userService.getUserById(userId); // Esto lanzará RuntimeException si no encuentra el usuario

            // Evitar que un usuario se siga a sí mismo
            if (follower.getId().equals(followed.getId())) {
                return ResponseEntity.badRequest().body("No puedes seguirte a ti mismo.");
            }

            followService.followUser(follower, followed);

            // Enviar notificación de Seguimiento
            Notification notification = new Notification();
            notification.setMessage(follower.getUsername()+"Te ha seguido");
            notification.setFollower(follower);
            notification.setFollowed(followed);
            notificationService.save(notification); // Guardar la notificación

            return ResponseEntity.ok("Has comenzado a seguir a " + followed.getUsername());
        } catch (RuntimeException e) {
            // Captura cuando el usuario no es encontrado
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Manejo genérico de otras excepciones
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al seguir al usuario.");
        }
    }

    // Dejar de seguir a un usuario
    @DeleteMapping("/unfollow/{userId}")
    public ResponseEntity<String> unfollowUser(@PathVariable Long userId, Principal principal) {
        try {
            User follower = userService.getUserByUsername(principal.getName());
            User followed = userService.getUserById(userId); // Esto lanzará RuntimeException si no encuentra el usuario

            // Puedes agregar una validación aquí para asegurarte de que el usuario realmente está siguiendo al otro usuario
            if (!followService.isFollowing(follower, followed)) {
                return ResponseEntity.badRequest().body("No estás siguiendo a " + followed.getUsername());
            }

            followService.unfollowUser(follower, followed);

            return ResponseEntity.ok("Has dejado de seguir a " + followed.getUsername());
        } catch (RuntimeException e) {
            // Captura cuando el usuario no es encontrado
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Manejo genérico de otras excepciones
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al dejar de seguir al usuario.");
        }
    }


    @GetMapping("/isFollowing/{userId}")
    public ResponseEntity<?> isFollowing(@PathVariable Long userId, Principal principal) {
        try {
            User follower = userService.getUserByUsername(principal.getName());
            User followed = userService.getUserById(userId); // Esto lanzará RuntimeException si no encuentra el usuario

            boolean isFollowing = followService.isFollowing(follower, followed);
            return ResponseEntity.ok(isFollowing);

        } catch (RuntimeException e) {
            // Aquí atrapamos la excepción cuando el usuario no es encontrado del UserService
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Manejo genérico de otras excepciones
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error al verificar el seguimiento: " + e.getMessage());
        }
    }


    // Obtener la lista de usuarios que sigue otro usuario por su ID
    @GetMapping("/following/{userId}")
    public ResponseEntity<?> getFollowing(@PathVariable Long userId) {
        try {
            User follower = userService.getUserById(userId); // Esto lanzará RuntimeException si no encuentra el usuario
            List<User> following = followService.getFollowing(follower);
            return ResponseEntity.ok(following);
        } catch (RuntimeException e) {
            // Captura cuando el usuario no es encontrado
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Manejo genérico de otras excepciones
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error al obtener los usuarios seguidos: " + e.getMessage());
        }
    }

    // Obtener la lista de seguidores de otro usuario por su ID
    @GetMapping("/followers/{userId}")
    public ResponseEntity<?> getFollowers(@PathVariable Long userId) {
        try {
            User followed = userService.getUserById(userId); // Esto lanzará RuntimeException si no encuentra el usuario
            List<User> followers = followService.getFollowers(followed);
            return ResponseEntity.ok(followers);
        } catch (RuntimeException e) {
            // Captura cuando el usuario no es encontrado
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Manejo genérico de otras excepciones
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error al obtener los seguidores: " + e.getMessage());
        }
    }

}
