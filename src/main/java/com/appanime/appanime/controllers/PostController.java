package com.appanime.appanime.controllers;
import com.appanime.appanime.models.*;
import com.appanime.appanime.security.services.UserService;
import com.appanime.appanime.services.*;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/posts")



public class PostController {
    @Autowired
    UserService userService;

    @Autowired
    PostService postService;

    @Autowired
    CommentService commentService;

    @Autowired
    FavoritePostsService favoritePostsService;

    @Autowired
    UtilService utilService;

    @Autowired
    EmailService emailService;

    @GetMapping("/user-posts/{username}")
    public Page<Post> getAllPostsByUser(@PathVariable String username,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        User user = userService.getUserByUsername(username);

        if (user != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Post> userPostsPage = this.postService.getAllByUser(user, pageable);
            return userPostsPage;
        } else {
            throw new RuntimeException("Usuario no encontrado: " + username);
        }
    }

    @GetMapping("/user-post/{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable Long postId) {
        Optional<Post> post = this.postService.getPostbyId(postId);

        if (post.isPresent()) {
            return ResponseEntity.ok(post.get()); // Devuelve 200 OK con la publicación encontrada
        } else {
            return ResponseEntity.notFound().build(); // Devuelve 404 Not Found si la publicación no existe
        }
    }


    //RECOGE LA IMAGEN DEL DIRECTORIO , DEVUELVE BYTE[] IMAGES
    @GetMapping("/{postId}/image/user/{username}")
    public ResponseEntity<byte[]> getPostImage(@PathVariable Long postId, @PathVariable String username) {
        Optional<Post> optionalPost = postService.getPostbyId(postId);

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            Path path = Paths.get("./images/posts", username, "/" + post.getId() + "/image.jpg");
            System.out.println(path);

            try {
                if (Files.exists(path)) {
                    byte[] image = Files.readAllBytes(path);
                    return ResponseEntity.ok(image);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } catch (IOException e) {
                // Manejar la excepción si ocurre un error de lectura
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/create-post")
    public ResponseEntity<Long> createPost(@RequestBody Post post) {
        try {

            Post savedPost = postService.save(post);
            Long postId = savedPost.getId();
            return ResponseEntity.ok(postId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PutMapping("edit/{postId}")
    public ResponseEntity<Post> editPost(@PathVariable Long postId, @RequestBody Post postEditRequest) {
        // Aquí deberías implementar la lógica para editar el título y subtítulo del post

        Optional<Post> optionalPost = this.postService.getPostbyId(postId);

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            // Verifica si se proporcionaron nuevos valores para el título y el subtítulo
            if (postEditRequest.getTitle() != null) {
                post.setTitle(postEditRequest.getTitle());
            }

            if (postEditRequest.getSubtitle() != null) {
                post.setSubtitle(postEditRequest.getSubtitle());
            }

            // Guarda el post editado
            Post editedPost = this.postService.save(post);

            return ResponseEntity.ok(editedPost);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    //SUBE UNA IMAGEN ( FILE ) AL DIRECTORIO
    @PostMapping("/upload-image/{postId}")
    public ResponseEntity<String> uploadPostImage(
            @RequestParam("file") MultipartFile file,
            @PathVariable("postId") Long postId,
            Principal principal
    ) {
        String username = principal.getName();
        String imageDirectory = "./images/posts/";

        try {
            if (!file.isEmpty()) {
                String[] allowedFiles = {"image/jpeg", "image/png", "image/webp"};
                if (Arrays.asList(allowedFiles).contains(file.getContentType())) {
                    // Validar tamaño máximo del archivo (10MB)
                    if (file.getSize() > 10 * 1024 * 1024) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El tamaño del archivo excede el límite permitido (10MB).");
                    }

                    Path userDirectory = Paths.get(imageDirectory, username);
                    Path postDirectory = userDirectory.resolve(String.valueOf(postId));

                    if (Files.notExists(postDirectory)) {
                        Files.createDirectories(postDirectory);
                    }

                    Path imagePath = postDirectory.resolve("image.jpg");
                    Files.copy(file.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);

                    return ResponseEntity.ok("Imagen cargada exitosamente para el post ID: " + postId);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El formato del archivo no es correcto.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Archivo vacío.");
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al cargar la imagen: " + e.getMessage());
        }
    }

    //delete
    @DeleteMapping("/delete-post/{id_post}")
    public ResponseEntity<String> eliminarPost(@PathVariable Long id_post,@RequestParam String username,Principal principal) throws MessagingException, MessagingException {
        User user=this.userService.getUserByUsername(principal.getName());
        boolean isAdmin=false;
        for (Role role : user.getRoles()) {

            if(role.getName().name().equals("ROLE_ADMIN")){
                isAdmin=true;break;
            }
        }

        if(isAdmin){ //si el usuario es admin entoncs se tiene que enviar un correo electronico al usuario del post
            //sendEmail
            User u=this.userService.getUserByUsername(username);

            String email=u.getEmail();
            String subject="dsa";
            String text = "Estimado  " + username +",\n" +
                    "\n" +
                    "Espero que este mensaje te encuentre bien. Te escribimos para informarte que tu post recientemente publicado en nuestra plataforma ha sido eliminado debido a violaciones de nuestras normas comunitarias.\n" +
                    "\n" +
                    "Entendemos que puede ser frustrante recibir esta notificación, pero es importante mantener un ambiente seguro y respetuoso para todos los usuarios de nuestra plataforma. Las razones específicas de la eliminación de tu post son las siguientes:\n" +
                    "\n" +
                    "- [Descripción detallada de las violaciones]\n" +
                    "\n" +
                    "Queremos recordarte que nuestro objetivo es promover un ambiente de respeto mutuo y colaboración entre todos los miembros de nuestra comunidad. Continuaremos aplicando nuestras normas de manera justa y consistente para garantizar la mejor experiencia posible para todos.\n" +
                    "\n" +
                    "Si tienes alguna pregunta o deseas apelar esta decisión, no dudes en ponerte en contacto con nuestro equipo de soporte a través de   [soporteanime@org o 865565464]. Estaremos encantados de ayudarte en lo que necesites.\n" +
                    "\n" +
                    "Gracias por tu comprensión y cooperación.\n" +
                    "\n" +
                    "Atentamente,\n" +
                    "[AnimeLov3rs]";

            this.emailService.sendConfirmationEmail(email,subject,text);
        }


        Optional<Post> existPost = this.postService.getPostbyId(id_post);
        System.out.println(username);
        if (existPost.isPresent()) {

            // ahora tenemos que eliminar tambien los comentarios que relacionan al post
            // Eliminar los comentarios relacionados con el post
            List<Comment> postComments = this.commentService.getCommentsByPost(existPost);
            for (Comment comment : postComments) {
                commentService.eliminarComment(comment);
            }
            List<FavoritePost> postFavoritePosts = favoritePostsService.getFavoritePostsByPost(existPost.get().getId());


            // Eliminar las instancias de FavoritePosts asociadas al post
            for (FavoritePost favoritePost : postFavoritePosts) {
                System.out.println(favoritePost.getId());
                favoritePostsService.eliminarFavoritePost(favoritePost);
            }


            // tambien hay que eliminar las notificaciones cuyo post esta relacionado
            postService.eliminarPost(existPost.get());

            System.out.println("estamos aquiiii");
            Path directoryPath = Paths.get("./images/posts", username, "/" + id_post);
            System.out.println("el directorio es " + directoryPath);
            try {
                this.utilService.eliminarArchivosDirectorio(directoryPath,"image.jpg");
                return ResponseEntity.ok("Post eliminada correctamente");
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar archivos");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra el Post");
        }
    }






}
