package com.appanime.appanime.controllers;


import com.appanime.appanime.models.FavoritePost;
import com.appanime.appanime.models.User;
import com.appanime.appanime.security.services.UserService;
import com.appanime.appanime.services.FavoritePostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/favoritePosts")
public class FavoritePosts {

    @Autowired
    UserService userService;
    @Autowired
    FavoritePostsService favoritePostsService;



    @GetMapping("/user/{username}")
    public ResponseEntity<List<FavoritePost>> getFavoritePostsByUser(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build(); // Devolver respuesta 404 si el usuario no se encuentra
        }
        List<FavoritePost> favoritePosts = favoritePostsService.getFavoritePostByUser(user);
        return ResponseEntity.ok(favoritePosts); // Devolver respuesta 200 con la lista de posts favoritos
    }

    @PostMapping("/user/favorite-posts")
    public ResponseEntity<String> addFavoritePost(Principal principal, @RequestBody FavoritePost favoritePost) {
        User user = userService.getUserByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.notFound().build(); // Devolver respuesta 404 si el usuario no se encuentra
        }
        System.out.println(favoritePost.getPost().getId());
        // Asignar el usuario al nuevo FavoritePost
        favoritePost.setUser(user);

        favoritePost.setPost(favoritePost.getPost());

        // Guardar el nuevo FavoritePost en la base de datos
        favoritePostsService.saveFavoritePost(favoritePost);

        return ResponseEntity.status(HttpStatus.CREATED).body("Favorite post added successfully"); // Devolver respuesta 201
    }
    @GetMapping("/isFavoritePost/post/{postId}")
    public ResponseEntity<Boolean> isFavoritePost(@PathVariable Long postId, Principal principal) {
        if (principal == null) {
            // No hay usuario logueado; podrías manejar esto de otra forma si es necesario
            return ResponseEntity.badRequest().body(false);
        }

        String username = principal.getName();
        User user = userService.getUserByUsername(username);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        boolean isFavorite = this.favoritePostsService.isFavorite(postId, user);
        return ResponseEntity.ok(isFavorite);
    }


    @DeleteMapping("/favorite-posts/{postId}")
    public ResponseEntity<Void> removeFavoritePost(@PathVariable Long postId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = principal.getName();
        // Asume que tienes un método para encontrar el usuario por username
        User user = userService.getUserByUsername(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
          this.favoritePostsService.eliminarFavoritePost(this.favoritePostsService.getFavoritePostByUserAndPost(postId,user));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
