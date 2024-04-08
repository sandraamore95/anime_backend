
package com.appanime.appanime.controllers;

import com.appanime.appanime.models.Friendship;
import com.appanime.appanime.models.Notification;
import com.appanime.appanime.models.User;
import com.appanime.appanime.payload.request.FriendshipRequest;
import com.appanime.appanime.security.services.UserService;
import com.appanime.appanime.services.FriendService;
import com.appanime.appanime.services.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/friends")
public class FriendsController {
    @Autowired
    FriendService friendshipService;
    @Autowired
    UserService userService;

    @Autowired
    NotificationService notificationService;



    @GetMapping("/all")
    public ResponseEntity<List<User>> getFriendsByUser(@RequestParam String username) {
        User user = this.userService.getUserByUsername(username);
        List<Friendship> friends = this.friendshipService.getAllFriendships(user);
        List<User> friendsUser = new ArrayList<>();
        for (Friendship f : friends) {
            User u = f.getFriend();
            friendsUser.add(u);
        }
        return ResponseEntity.ok(friendsUser);
    }


    @GetMapping("/existPending/{userId}")
    public Boolean existPending( @PathVariable Long userId,Principal principal) {
        try {
            User user = userService.getUserByUsername(principal.getName());
            User friend = userService.getUserById(userId);
            // Validar si los usuarios son distintos antes de verificar la solicitud pendiente
            if (user.getId().equals(friend.getId())) {
                return false;
            }
            System.out.println(this.friendshipService.hasPending(user, friend));
            return this.friendshipService.hasPending(user, friend);
        } catch (Exception e) {
            return false;
        }
    }
    @GetMapping("/existFriendShip/{userId}")
    public Boolean existFriendship(@PathVariable Long userId,Principal principal) {
        try {
            User user = userService.getUserByUsername(principal.getName());
            User friend = userService.getUserById(userId);

            // Validar si los usuarios son distintos antes de verificar la amistad
            if (user.getId().equals(friend.getId())) {
                return false;
            }
            return this.friendshipService.hasFriendship(user, friend);
        } catch (Exception e) {
            return false;
        }

    }

    @PostMapping("/send-request/{userId}")
    public ResponseEntity<String> sendFriendRequest(@PathVariable Long userId, Principal principal) {
        try {
            User sender = userService.getUserByUsername(principal.getName());
            User receiver = userService.getUserById(userId);

            friendshipService.sendFriendRequest(sender, receiver);

            // Enviar notificación de amistad
            Notification notification = new Notification();
            notification.setMessage("Has recibido una solicitud de amistad");
            notification.setSender(sender);
            notification.setReceiver(receiver);
            notificationService.save(notification); // Guardar la notificación

            return ResponseEntity.ok("Solicitud de amistad enviada correctamente.");
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al enviar solicitud de amistad. Por favor, inténtalo de nuevo más tarde.");
        }
    }



    @DeleteMapping("/cancel-request/{userId}")
    public ResponseEntity<String> cancelFriendRequest(@PathVariable Long userId, Principal principal) {
        String username = principal.getName();

        try {
            User sender = userService.getUserByUsername(username);
            User receiver = userService.getUserById(userId);

            //recorremos las notificaciones sender x receiver y buscamos la de tipo _> Notification para eliminar solo los registros correctos.
            deleteNotificationRequest(sender,receiver);
            // cancelar la solicitud de amistad utilizando el servicio
            friendshipService.cancelFriendRequest(sender, receiver);

            return ResponseEntity.ok("Solicitud de amistad cancelada correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PostMapping ("/accept-request/{userId}")
    public ResponseEntity<String> acceptFriendRequest(@PathVariable Long userId, Principal principal) {
        String username = principal.getName();

        try {
            User sender = userService.getUserById(userId);
            User receiver = userService.getUserByUsername(username);

            deleteNotificationRequest(sender,receiver);

            // Aceptar la solicitud de amistad utilizando el servicio
            friendshipService.acceptFriendRequest(sender, receiver);

            return ResponseEntity.ok("Solicitud de amistad aceptada correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @DeleteMapping("/reject-request/{userId}")
    public ResponseEntity<String> rejectFriendRequest(@PathVariable Long userId, Principal principal) {
        String username = principal.getName();

        try {
            User sender = userService.getUserByUsername(username);
            User receiver = userService.getUserById(userId);

            deleteNotificationRequest(receiver,sender);
            // Rechazar la solicitud de amistad utilizando el servicio
            friendshipService.cancelFriendRequest(receiver, sender);

            return ResponseEntity.ok("Solicitud de amistad rechazada correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete-friendship/{userId}")
    public ResponseEntity<String> deleteFriendship(@PathVariable Long userId, Principal principal) {
        String username = principal.getName();
        try {
            User sender = userService.getUserByUsername(username);
            User receiver = userService.getUserById(userId);

            if (receiver == null) {
                return ResponseEntity.badRequest().body("El usuario con ID " + userId + " no existe");
            }

            if (!friendshipService.hasFriendship(sender, receiver)) {
                return ResponseEntity.badRequest().body("No se puede borrar porque no son amigos");
            }

            this.friendshipService.removeFriendship(sender, receiver);
            return ResponseEntity.ok("Solicitud de amistad eliminada correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al intentar eliminar la amistad: " + e.getMessage());
        }
    }
    // endpoint para ver si quien ha realizado la solicitud es el usuario autenticado o no
    @GetMapping("/friendship-request-sender/{userId}")
    public ResponseEntity<User> getFriendshipRequestSender(@PathVariable Long userId, Principal principal) {
        try {
            System.out.println("ESTAMOS AQUI EN SENDER ENDPOINT");
            User sender = userService.getUserByUsername(principal.getName());
            User receiver = userService.getUserById(userId);
            System.out.println(sender.getUsername());
            System.out.println(receiver.getUsername());
            FriendshipRequest friendshipRequest = friendshipService.getFriendshipRequest(sender, receiver);

            if (friendshipRequest != null) {
                System.out.println("el usuario es sender");
                // El usuario autenticado es el remitente de la solicitud
                return ResponseEntity.ok(friendshipRequest.getSender());
            } else {
                System.out.println("el usuario  NOO  es sender");
                // El usuario autenticado no es el remitente de la solicitud
                return null;
            }
        } catch (Exception e) {
            // Manejar errores y devolver un código de estado apropiado
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/friendship-request-receiver/{userId}")
    public ResponseEntity<Boolean> isActingAsReceiver(@PathVariable Long userId, Principal principal) {
        try {
            String username = principal.getName();
            User loggedInUser = userService.getUserByUsername(username);
            User otherUser = userService.getUserById(userId);

            // Verificar si hay una solicitud pendiente donde el usuario autenticado es el receptor
            boolean isReceiver = friendshipService.isReceiverInPendingRequest(loggedInUser, otherUser);

            return ResponseEntity.ok(isReceiver);
        } catch (Exception e) {
            // Manejar errores y devolver un código de estado apropiado
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }



    private void deleteNotificationRequest(User sender, User receiver) {
        List<Notification> notifications = notificationService.findNotifications(sender, receiver);
        for (Notification notification : notifications) {
            if ("Notification".equals(notification.getDtype())) {
                this.notificationService.eliminar(notification);
            }
        }
    }



}




