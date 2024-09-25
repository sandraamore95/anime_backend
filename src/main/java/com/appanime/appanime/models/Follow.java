
package com.appanime.appanime.models;
import jakarta.persistence.*;
import java.util.Optional;

@Entity
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
// Constructor, getters y setters
}
