package com.appanime.appanime.models;



import jakarta.persistence.*;


import java.util.Optional;

@Entity
public class Anime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long malId; // Campo mal_id único

    @ManyToOne
    private User usuario; // Relación con Usuario

    public Long getMalId() {
        return malId;
    }

    public void setMalId(Long malId) {
        this.malId = malId;
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }


// Otras propiedades que desees almacenar

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }




}
