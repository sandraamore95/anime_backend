package com.appanime.appanime.services;

import com.appanime.appanime.models.Anime;
import com.appanime.appanime.models.Friendship;
import com.appanime.appanime.repository.AnimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnimeService {


    @Autowired
    AnimeRepository animeRepository;
    public List<Anime> getAllAnimes(){
        return this.animeRepository.findAll();
    }

    public Optional<Anime> getAnimeById(Long anime_id) {
        return this.animeRepository.findById(anime_id);
    }
    public boolean existAnimeUser(Long malId,String nombreUsuario) {
        Optional<Anime> existAnime = this.animeRepository.findByMalIdAndUsuarioUsername(malId, nombreUsuario);
        return existAnime.isPresent();
    }
    public void guardarAnime(Anime anime) {
        // Implementa la lógica para guardar la anime en la base de datos
        animeRepository.save(anime);
    }

    public void eliminarAnime(Anime anime) {
        animeRepository.delete(anime);
    }

    public List<Anime> getAnimesFavoritosUsuario(String nombreUsuario) {
        return animeRepository.findByUsuarioUsername(nombreUsuario);
    }


    public Optional<Anime> getAnimeByMalIdUsuario(Long malId, String nombreUsuario) {
        return this.animeRepository.findByMalIdAndUsuarioUsername(malId,nombreUsuario);

    }


}


