package com.appanime.appanime.models;
import jakarta.persistence.*;



import java.time.LocalDateTime;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String subtitle;

    @ManyToOne
    @JoinColumn(name = "user_id") // Nombre de la columna que representa la relación con Usuario
    private User author; // Relación con la entidad Usuario

    private LocalDateTime createdAt;

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }


    // Otros atributos, constructores, getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }


    // Constructor
    public Post() {
        this.createdAt = LocalDateTime.now(); // Establecer la fecha y hora actual como fecha de creación por defecto
    }
}


// Getters y setters para los atributos
// ...
