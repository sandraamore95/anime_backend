package com.appanime.appanime.controllers;

import com.appanime.appanime.models.*;
import com.appanime.appanime.security.services.UserService;

import com.appanime.appanime.services.CommentService;
import com.appanime.appanime.services.EmailService;
import com.appanime.appanime.services.NotificationService;
import com.appanime.appanime.services.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


import jakarta.mail.MessagingException;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/comments")

public class CommentController {
    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;
    @Autowired
    NotificationService notificationService;
    @Autowired
    PostService postService;

    @Autowired
    EmailService emailService;
    //getAll()
    @GetMapping("/all")
    public List<Comment> getAll(){
        return this.commentService.getAll();
    }


    //getAllByUser()
    @GetMapping("/user-comments/{username}")
    public List<Comment> getAllCommentsByUser(@PathVariable String username) {
        User user = userService.getUserByUsername(username);

        if (user != null) {
            List<Comment> userComments = this.commentService.getAllbyUser(user);

            // Ordenar la lista de publicaciones por fecha de creación (createdAt) en orden descendente
            List<Comment> sortedComments = userComments.stream()
                    .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                    .collect(Collectors.toList());

            return sortedComments;
        } else {
            throw new  RuntimeException("Usuario no encontrado : " + username);
        }
    }

    @GetMapping("/user-comments-post/{postId}")
    public List<Comment> getAllCommentsByPost(@PathVariable Long postId) {

            List<Comment> userComments = this.commentService.getAllbyPost(postId);

            // Ordenar la lista de publicaciones por fecha de creación (createdAt) en orden descendente
            List<Comment> sortedComments = userComments.stream()
                    .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                    .collect(Collectors.toList());
            return sortedComments;
    }

    //getById()

    @GetMapping("/user-comment/{commentId}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long commentId) {
        Optional<Comment> comment = this.commentService.getCommentbyId(commentId);

        if (comment.isPresent()) {

            return ResponseEntity.ok(comment.get()); // Devuelve 200 OK con la publicación encontrada
        } else {
            return ResponseEntity.notFound().build(); // Devuelve 404 Not Found si la publicación no existe
        }
    }



    //addComment()

    @PostMapping("/create-comment")
    public ResponseEntity<Comment>  createComment(@RequestBody Comment commentRequest) {
        try {
            // Valida los datos del comentario
            if (commentRequest.getText() == null || commentRequest.getText().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            // Crea una instancia de Comment con los datos recibidos
            Comment comment = new Comment();
            comment.setText(commentRequest.getText());
            comment.setAuthor(commentRequest.getAuthor());
            comment.setPost(commentRequest.getPost());
            Comment savedComment = commentService.save(comment);

            //creamos una notificacion asociada a ese comentario  y la configuramos
            // Obtener la notificación asociada desde el comentario
            Notification.CommentNotification notificationComment = savedComment.getNotification();

            // Si la notificación asociada es nula, crear una nueva instancia de notificación
            if (notificationComment == null) {
                notificationComment = new Notification.CommentNotification();
                notificationComment.setMessage("Has recibido un comentario en tu publicacion!");
                notificationComment.setSender(savedComment.getAuthor());
                User postAuthor = this.postService.getUserByPostId(savedComment.getPost().getId());
                notificationComment.setReceiver(postAuthor);  // Asumiendo que postAuthor está definido en tu código
                notificationComment.setComment(savedComment);
                this.notificationService.save(notificationComment);
            }


            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String json;

            try {
                json = objectMapper.writeValueAsString(savedComment);
                System.out.println(json);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Devuelve una respuesta exitosa con el comentario guardado
            return ResponseEntity.ok(savedComment);
        } catch (Exception e) {
            // Maneja excepciones y devuelve una respuesta de error
            e.printStackTrace();  // Considera mejorar el manejo de errores en producción
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    //deleteById()
    @DeleteMapping("/delete-comment/{id_comment}")
    public ResponseEntity<String> eliminarComment(@PathVariable Long id_comment,Principal principal) throws MessagingException {

        User user=this.userService.getUserByUsername(principal.getName());
        //sacar los roles
        boolean isAdmin=false;
        for (Role role : user.getRoles()) {

            if(role.getName().name().equals("ROLE_ADMIN")){
                isAdmin=true;break;
            }

        }

        Optional<Comment> existComment = this.commentService.getCommentbyId(id_comment);

        if (existComment.isPresent()) {
            // Eliminar el comentario y la notificacion asociada por CASCADE
            commentService.eliminarComment(existComment.get());

            if(isAdmin){
                //enviar correo
                String username= existComment.get().getAuthor().getUsername();
                String email=existComment.get().getAuthor().getEmail();
                String subject="dsa";
                String text = "Estimado " + username + "El siguiente comentario `["+existComment.get().getText() +"]ha sido eliminado.";

                this.emailService.sendConfirmationEmail(email,subject,text);
            }
            return ResponseEntity.ok("Comentario eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra el Comentario");
        }
    }











}
