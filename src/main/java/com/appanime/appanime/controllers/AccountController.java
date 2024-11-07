package com.appanime.appanime.controllers;

import com.appanime.appanime.models.*;
import com.appanime.appanime.security.services.UserService;
import com.appanime.appanime.services.*;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    EmailService emailService;
    @Autowired
    AccountService accountService;
    @Autowired
    CommentService commentService;
    @Autowired
    NotificationService notificationService;
    @Autowired
    PostService postService;
    @Autowired
    FavoritePostsService favoritePostsService;
    @Autowired
    AnimeService animeService;
    @Autowired
    FollowService followService;
    @Autowired
    ProfileService profileService;

    @Autowired
    UtilService utilService;

    @ResponseBody
    @PostMapping("/delete-account")
    public String deleteAccount(@RequestBody Map<String, String> requestBody, RedirectAttributes redirectAttributes,Principal principal) throws IOException {

        User user= this.userService.getUserByUsername(principal.getName());
        boolean isAdmin=false;
        for (Role role : user.getRoles()) {
            if(role.getName().name().equals("ROLE_ADMIN")){
                isAdmin=true;break;
            }
        }
        if(isAdmin){
            String email = requestBody.get("email");
            deleteUserRelationships(this.userService.getUserByEmail(email));
        }else{
            String email = requestBody.get("email");
            String token = generateToken();
            generateAccount(token,email); //guardamos el token en la BD

            String confirmationLink = "http://localhost:8080/api/account/confirm-delete-account?token=" + token + "&email=" + email;
            String message = "Por favor, haz clic en el siguiente enlace para confirmar la eliminación de tu cuenta: " + confirmationLink;
            String subject = "Confirmación de eliminacion de cuenta";

            try {
                emailService.sendConfirmationEmail(email, subject, message);
                redirectAttributes.addFlashAttribute("successMessage", "Se ha enviado un correo electrónico de confirmación. Por favor, revisa tu bandeja de entrada.");
            } catch (MessagingException e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error al enviar el correo electrónico de confirmación.");
            }
        }
        return "redirect:/";
    }
    @GetMapping("/redirect")
    public String confirmDeleteAccount2(@RequestParam("token") String token, @RequestParam("email") String email, RedirectAttributes redirectAttributes) throws IOException {

        return "redirect:https://www.google.com/";
    }



    @GetMapping("/confirm-delete-account")
    public String confirmDeleteAccount(@RequestParam("token") String token, @RequestParam("email") String email, RedirectAttributes redirectAttributes) throws IOException {
        Account confirmacionCuenta = this.accountService.getAccountByToken(token);
        String message;
        // Implement logic to verify token and email and delete account

        if (confirmacionCuenta != null && confirmacionCuenta.getFechaExpiracion().isAfter(LocalDateTime.now())) {
            System.out.println("se ha eliminado ");
            message = "Your account has been deleted successfully.";
            this.accountService.delete(confirmacionCuenta);
            deleteUserRelationships(this.userService.getUserByEmail(email));
        }
        else {
            message = "An error occurred. Your account deletion failed.";
        }

        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:http://localhost:4200/auth/login"; // Replace with your confirmation page path
    }

    @Transactional
    public void deleteUserRelationships(User user) throws IOException {

        System.out.println("DELETE ACCCOUNT");


        // Eliminar todas las relaciones de amistad
        deleteFollow(user);


        //Eliminar las publicaciones favoritas relacionadas con el usuario
        deleteFavoritePosts(user);

        // Eliminar todas las notificaciones relacionadas con el usuario
        deleteNotifications(user);

        // Eliminar todas los comentarios relacionados con el usuario
        deleteComments(user);


        // Eliminar todos los posts realizados por el usuario + RUTA POST
        deletePost(user);


        //Eliminar todos los animes favoritos relacionados con el Usuario
        deleteAnimeFavorite(user);


        //Eliminar el profile relacionado con el usuario
        Profile profile_user=this.profileService.getProfileByUser(user);
        this.profileService.eliminarProfile(profile_user);

        //Eliminar la carpeta de AVATAR
        Path directoryPath = Paths.get("./images/avatar", user.getUsername(), "/" );
        this.utilService.eliminarArchivosDirectorio(directoryPath,"avatar.png");

        //Eliminar el usuario
        this.userService.deleteUser(user);


    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody String email) {
        // Validar el email
        if(email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El email no puede estar vacío");
        }
        try {
            User user = userService.getUserByEmail(email);
            try {
                String token = generateToken();
                generateAccount(token, email);
                String confirmationLink = "http://localhost:4200/pages/reset-passw?token=" + token;
                String subject = "Confirmación de cambiar contraseña";
                String message = "Por favor, haz clic en el siguiente enlace para confirmar la modificación de la contraseña: " + confirmationLink;

                emailService.sendConfirmationEmail(email, subject, message);
                return ResponseEntity.ok("Se ha enviado un correo electrónico de confirmación. Por favor, revisa tu bandeja de entrada.");
            } catch (MessagingException e) {
                return ResponseEntity.internalServerError().body("Error al enviar el correo electrónico de confirmación.");
            }
        }catch (Exception e) {
            // Aquí podrías loguear el error
            return ResponseEntity.internalServerError().body("Error User not found");
        }
    }
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> requestBody) {
        String token = requestBody.get("token");
        String newPassword = requestBody.get("newPassword");
        Account account = accountService.findByToken(token);
        if (account != null && !account.isExpired()) {
            // Si se encuentra el token y no ha expirado, puedes utilizar el email asociado al token
            String email = account.getEmail();
            // Luego, puedes buscar el usuario por el email
            User user = userService.getUserByEmail(email);
            if (user != null) {
                // Actualizar la contraseña del usuario
                user.setPassword(encoder.encode(newPassword));
                this.userService.save(user);
                //Eliminar el token de la entidad Account una vez que se haya utilizado
                accountService.delete(account);
                return ResponseEntity.ok("Contraseña restablecida exitosamente");
            } else {
                return ResponseEntity.badRequest().body("No se encontró ningún usuario asociado al token");
            }
        } else {
            return ResponseEntity.badRequest().body("El token no es válido o ha expirado");
        }
    }

    @ResponseBody
    @GetMapping("/validate-token")
    public Boolean validateToken(@RequestParam String token) {
        Account account = accountService.findByToken(token);
        if (account != null && !account.isExpired()) {
            return true;
        } else {
            return false; // invalido o ha expirado
        }
    }
    public  String generateToken() {
        return UUID.randomUUID().toString();
    }

    public void generateAccount(String token , String email) {
        LocalDateTime expirationDate = LocalDateTime.now().plusDays(1);
        Account account = new Account(token, email, expirationDate);
        accountService.save(account);

    }

    private void deleteFollow(User user) {
        followService.deleteFollowsAndRelatedRequestsByUserId(user.getId());
    }

    //Eliminar las publicaciones favoritas que esta usando otro usuario y el post el del usuario
    private void deleteFavoritePosts(User user) {
        List<FavoritePost> postFavoritePosts = favoritePostsService.getFavoritePostByUser(user);
        for (FavoritePost favoritePost : postFavoritePosts) {
            favoritePostsService.eliminarFavoritePost(favoritePost);
        }
    }

    public void deleteNotifications(User user){
        this.notificationService.deleteNotificationsByUser(user.getId());
    }

    //Get comentarios byUserSenderOrPostAuthor (Post actua como de receiver)
    public  void deleteComments(User user) {
        List<Comment> comments = commentService.getCommentsByUserorPostAuthor(user.getId());
        for (Comment comment : comments) {
            // Realiza alguna acción con cada comentario, por ejemplo, imprimir su texto
            System.out.println("los comentarios que han salido son : ");
            System.out.println(comment.getAuthor().getId());
            this.commentService.eliminarComment(comment);
        }

    }
    public void deletePost(User user) throws IOException {
        List<Post> posts_user= this.postService.getAllPostsbyUser(user.getId());

        for (Post post : posts_user) {
            System.out.println("ID del post: " + post.getId());

            List<FavoritePost> postFavoritePosts_bypost = favoritePostsService.getFavoritePostsByPost(post.getId());
            // Eliminar las instancias de FavoritePosts asociadas al post
            for (FavoritePost favoritePost : postFavoritePosts_bypost) {
                favoritePostsService.eliminarFavoritePost(favoritePost);
            }

            this.postService.eliminarPost(post);
            Path directoryPath = Paths.get("./images/posts", user.getUsername(), "/" + post.getId());
            this.utilService.eliminarArchivosDirectorio(directoryPath,"imagen.jpg");
            //si no tiene nada en su interior tambien eliminar la carpeta de username post

        }
    }

    public void deleteAnimeFavorite(User user){
        List <Anime> animes_favoritos= this.animeService.getAnimesFavoritosUsuario(user.getUsername());
        for (Anime anime : animes_favoritos) {
            System.out.println(anime.getId());
            this.animeService.eliminarAnime(anime);
        }
    }










}