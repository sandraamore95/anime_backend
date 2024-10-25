package com.appanime.appanime.controllers;

import com.appanime.appanime.models.*;
import com.appanime.appanime.security.services.UserService;
import com.appanime.appanime.services.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    @Autowired
    NotificationService notificationService;
    @Autowired
    UserService userService;

    @GetMapping("/all")
    public List<Notification> getAll() {
        return this.notificationService.getAll();
    }

    //obtiene todas las notificaciones dependiendo si es principal , o user
    @GetMapping("/user-notifications")
    public List<Notification> getAllNotificationsByUser(Principal principal, @RequestParam("role") String role) {
        String username = principal.getName();
        User user = userService.getUserByUsername(username);

        List<Notification> notifications;

        switch (role) {
            case "receiver":
                notifications = notificationService.getAllByUserFollowed(user);
                break;
            case "sender":
                notifications = notificationService.getAllByUserFollower(user);
                break;
            default:
                notifications = Collections.emptyList(); // Devolver una lista vacía en este caso
                break;
        }
        return notifications;
    }

    @DeleteMapping("/delete-notification/{id_notification}")
    public ResponseEntity<String> eliminarNotificacion(@PathVariable Long id_notification) {
        try {
            Optional<Notification> exist_notification = notificationService.getNotificationById(id_notification);
            if (exist_notification.isPresent()) {
                Notification.CommentNotification commentNotification = (Notification.CommentNotification) exist_notification.get();

                // Crear una nueva instancia de Notification
                Notification notification = new Notification();

                // Copiar los atributos relevantes de la instancia de CommentNotification a la instancia de Notification
                notification.setId(commentNotification.getId());
                notification.setMessage(commentNotification.getMessage());
                notification.setFollower(commentNotification.getFollower());
                notification.setFollowed(commentNotification.getFollowed());

                notificationService.save(notification); // Guardar la notificación



                // Eliminar la instancia original de CommentNotification
                notificationService.eliminar(notification);
                return ResponseEntity.ok("Notification deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notification with ID " + id_notification + " not found");
            }
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting notification");
        }
    }


}



