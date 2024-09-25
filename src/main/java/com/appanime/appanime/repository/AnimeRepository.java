package com.appanime.appanime.repository;

import com.appanime.appanime.models.Anime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnimeRepository extends JpaRepository<Anime, Long> {


    Optional<Anime> findByMalIdAndUsuarioUsername(Long malId, String username);
    List<Anime> findByusuario_id(Long id);
    List<Anime> findByUsuarioUsername(String username);
 //   List<Anime> findByMalIdAndUsuarioUsername(Long malId, String username);

}
