package com.appanime.appanime.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String text;

    // Relación con la entidad de usuario, puedes ajustar la anotación según tu estructura de usuario
    @ManyToOne
    private User author;

    @ManyToOne
    private Post post;
    @OneToOne(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Notification.CommentNotification notification;

    public Notification.CommentNotification getNotification() {
        return notification;
    }

    public void setNotification(Notification.CommentNotification notification) {
        this.notification = notification;
        if (notification != null) {
            notification.setComment(this);
        }
    }

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


// Getters y setters u otros campos que puedas necesitar

    // Constructor(s)
    public Comment() {
        this.createdAt = LocalDateTime.now();
        this.notification = new Notification.CommentNotification();
        this.notification.setComment(this);
    }


    // Métodos adicionales si los requieres
}
