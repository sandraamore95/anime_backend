package com.appanime.appanime.models;
import jakarta.persistence.*;

@Entity
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000) // Puedes ajustar este valor según tus necesidades
    private String about;
    private String username;
    private String fullName;

    private String FacebookInProfile;
    private String twitterProfile;
    private String InstragramProfile;
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Redes sociales


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }



    public String getFacebookInProfile() {
        return FacebookInProfile;
    }

    public void setFacebookInProfile(String facebookInProfile) {
        FacebookInProfile = facebookInProfile;
    }

    public String getTwitterProfile() {
        return twitterProfile;
    }

    public void setTwitterProfile(String twitterProfile) {
        this.twitterProfile = twitterProfile;
    }

    public String getInstragramProfile() {
        return InstragramProfile;
    }

    public void setInstragramProfile(String instragramProfile) {
        InstragramProfile = instragramProfile;
    }



    // Getters y setters, y otros campos que puedas necesitar

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }




    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Constructor(s)
    // ...
}