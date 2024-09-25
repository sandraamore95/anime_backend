package com.appanime.appanime.controllers;

import com.appanime.appanime.models.Anime;
import com.appanime.appanime.models.User;
import com.appanime.appanime.security.services.UserService;
import com.appanime.appanime.services.AnimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/anime")
public class AnimeController {
    @Autowired
    AnimeService animeService;
    @Autowired
    UserService userService;

    @GetMapping
    public List<Anime> getAll(){
        return this.animeService.getAllAnimes();
    }

    @PostMapping("/addFavorite")
    public ResponseEntity<String> agregarAnime(@RequestBody Anime anime, Principal principal) {

        String username = principal.getName();
        // Verificar si el anime ya está en la lista de favoritos del usuario
        Optional<Anime> existAnime = animeService.getAnimeByMalIdUsuario(anime.getMalId(), username);
        if (existAnime.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("El anime ya está en tu lista de favoritos");
        }
        // Obtener el usuario por su nombre de usuario
        User user = userService.getUserByUsername(username);
        if (user != null) {
            // Establecer la asociación entre el usuario y el anime
            anime.setUsuario(user);
            animeService.guardarAnime(anime);
            return ResponseEntity.ok("Anime agregado a la lista de favoritos");
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("No se pudo encontrar al usuario");
        }
    }

    @PostMapping("/deleteFavorite")
    public ResponseEntity<String> eliminarAnime(@RequestBody Anime anime, Principal principal) {
        String username = principal.getName();

        // Verificar si el anime está en la lista de favoritos del usuario
        Optional<Anime> existAnime = animeService.getAnimeByMalIdUsuario(anime.getMalId(), username);
        if (existAnime.isPresent()) {
            // Eliminar el anime de la lista de favoritos del usuario
            animeService.eliminarAnime(existAnime.get());
            return ResponseEntity.ok("Anime eliminado de la lista de favoritos");
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("El anime no está en la lista de favoritos");
        }
    }

    @GetMapping("/usuario/{username}")
    public ResponseEntity<List<Anime>> getAnimesFavoritosUsuario(@PathVariable String username) {
        List<Anime> animesFavoritos = animeService.getAnimesFavoritosUsuario(username);
        return ResponseEntity.ok(animesFavoritos);
    }




    //este endpoint sirve para sacar en el fronted - agregar / eliminar anime
    @PostMapping("/exists-fav")
    public boolean existAnimeUser(@RequestBody Anime anime, Principal principal) {
        return animeService.existAnimeUser(anime.getMalId(), principal.getName());
    }

}
