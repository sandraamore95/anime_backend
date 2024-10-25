package com.appanime.appanime.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype", length = 100)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public String getDtype() {
        return dtype;
    }

    public void setDtype(String dtype) {
        this.dtype = dtype;
    }

    @Column(insertable = false, updatable = false)
    private String dtype;
    private String message;

    // Usuario que sigue a otro usuario
    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    // Usuario que es seguido
    @ManyToOne
    @JoinColumn(name = "followed_id", nullable = false)
    private User followed;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getFollower() {
        return follower;
    }

    public void setFollower(User follower) {
        this.follower = follower;
    }

    public User getFollowed() {
        return followed;
    }

    public void setFollowed(User followed) {
        this.followed = followed;
    }

// Getters, setters y constructores

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @Entity
    @DiscriminatorValue("CommentNotification")
    public static class CommentNotification extends Notification {
        // Otros atributos y métodos

        @OneToOne
        @JoinColumn(name = "comment_id", unique = true)
        private Comment comment;

        public Comment getComment() {
            return comment;
        }

        public void setComment(Comment comment) {
            this.comment = comment;
        }
    }

}