package com.appanime.appanime.controllers;

import com.appanime.appanime.models.*;
import com.appanime.appanime.security.services.UserService;
import com.appanime.appanime.services.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
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
                notifications = notificationService.getAllbyUserReceiver(user);
                break;
            case "sender":
                notifications = notificationService.getAllbyUserSender(user);
                break;
            default:
                notifications = Collections.emptyList(); // Devolver una lista vacía en este caso
                break;
        }
        return notifications;
    }


    //eliminar notificacion tanto de sender-receiver
    //1 . sender ( cancela la solicitud )
    //2. receiver (acepta/rechaza)



    //FUNCIONA PARA NOTIFICACIONES DE TIPO -> REQUEST
    // NO FUNCIONA PARA NOTIFICACIOENS DE TIPO COMMENT.

    @DeleteMapping("/delete-notification/{id_notification}")
    public ResponseEntity<String> eliminarNotificacion(@PathVariable Long id_notification, @RequestParam("role") String role) {

        System.out.println(role);
        System.out.println(id_notification);
           this.notificationService.eliminar(role ,id_notification);


            System.out.println("SE HA ELIMINADO DE LA BD");
            return ResponseEntity.ok("Notification deleted successfully");


    }
}



