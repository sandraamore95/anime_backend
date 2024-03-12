package com.appanime.appanime.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private String email;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaExpiracion;

    // Constructor, getters y setters

    // Constructor vacío
    public Account() {
    }

    // Constructor con parámetros
    public Account(String token, String email, LocalDateTime fechaExpiracion) {
        this.token = token;
        this.email = email;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaExpiracion = fechaExpiracion;
    }

    // Getters y setters
    // También puedes generarlos automáticamente con tu IDE o herramienta de desarrollo

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public boolean isExpired() {
        // Obtener la fecha y hora actuales
        LocalDateTime currentDateTime = LocalDateTime.now();
        // Comparar con la fecha de expiración del token
        return currentDateTime.isAfter(fechaExpiracion);
    }

}
