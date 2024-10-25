package com.appanime.appanime.repository;

import com.appanime.appanime.models.Anime;
import com.appanime.appanime.models.Notification;
import com.appanime.appanime.models.Post;
import com.appanime.appanime.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {


    List<Notification> findByFollower(User follower);
    List<Notification> findByFollowed(User followed);


    List<Notification> findByFollowerOrFollowed(User follower, User followed);


    @Query("SELECT n FROM Notification n " +
            "WHERE EXISTS (SELECT 1 FROM Notification n2 " +
            "              WHERE n.follower = n2.followed AND n.followed = n2.follower)")
    List<Notification> findRelatedNotifications(User follower, User followed);


    Notification findByFollowedAndFollower(User followed, User follower);
    Notification findByFollowerAndFollowed(User follower, User followed);

    // Obtener todas las notificaciones para un usuario que es seguido
    List<Notification> findAllByFollowed(User followed);

    // Obtener todas las notificaciones para un usuario que es seguidor
    List<Notification> findAllByFollower(User follower);


    void deleteByFollowerIdOrFollowedId(Long followerId, Long followedId);
}
