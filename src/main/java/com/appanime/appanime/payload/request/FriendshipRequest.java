package com.appanime.appanime.payload.request;

import com.appanime.appanime.models.User;

import jakarta.persistence.*;
@Entity
@Table(name = "friendship_requests")
public class FriendshipRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    // Otros campos, constructores, getters y setters

    public FriendshipRequest() {
        // Constructor por defecto
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public FriendshipRequest(User sender, User receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    // Getters y setters
}