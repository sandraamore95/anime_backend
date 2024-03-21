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


    Notification findBySender(User user);
    Notification findByReceiver(User user);


    List<Notification> findBySenderOrReceiver(User sender, User receiver);

    @Query("SELECT n FROM Notification n " +
            "WHERE EXISTS (SELECT 1 FROM Notification n2 " +
            "              WHERE n.sender = n2.receiver AND n.receiver = n2.sender)")
    List<Notification> findRelatedNotifications(User sender, User receiver);
    Notification findByReceiverAndSender(User receiver, User sender);
    Notification findBySenderAndReceiver(User sender, User receiver);
    List<Notification> findAllByReceiver(User user);

    List<Notification> findAllBySender(User user);



    void deleteBySenderIdOrReceiverId(Long senderId,Long receiverId);
}
