package com.appanime.appanime.services;

import com.appanime.appanime.models.Notification;
import com.appanime.appanime.models.User;
import com.appanime.appanime.repository.NotificationCommentRepository;
import com.appanime.appanime.repository.NotificationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    NotificationCommentRepository notificationCommentRepository;


    public List<Notification> getAll() {
      return  this.notificationRepository.findAll();
    }

    public List<Notification> getAllbyUserReceiver(User user) {
        return this.notificationRepository.findAllByReceiver(user);

    }
    public List<Notification> getAllbyUserSender(User user) {
        return this.notificationRepository.findAllBySender(user);

    }

    public List<Notification> getNotificationbySenderorReceiver(User sender,User receiver) {
        return this.notificationRepository.findBySenderOrReceiver(sender,receiver);

    }
    public Optional<Notification> getNotificationById(Long id_notification){
            return this.notificationRepository.findById(id_notification);

    }
    public List<Notification> findRelatedNotifications(User sender, User receiver) {
        return notificationRepository.findRelatedNotifications(sender,receiver);
    }
    public void eliminarNotification(Notification notification) {
        notificationRepository.delete(notification);
    }


    //tenemos que ver que tipo de notificacion es :
    public void eliminar(String role, Long id_notification){
        if(role.equals("Notification")){
            Optional<Notification> notification= this.notificationRepository.findById(id_notification);
            notification.ifPresent(value -> notificationRepository.delete(value));
        }
        else{
            System.out.println("estamos aquiiiiiiiii en cooommdeb");
            Optional<Notification.CommentNotification> notification=this.notificationCommentRepository.findById(id_notification);
            System.out.println(notification.get().getId());
            System.out.println(notification.get().getComment().getId());
            this.notificationCommentRepository.delete(notification.get());
           // notification.ifPresent(commentNotification -> notificationCommentRepository.delete(commentNotification));
        }
    }


    public Notification getNotificationbyReceiverandSender(User receiver, User sender) {
        return this.notificationRepository.findByReceiverAndSender(receiver,sender);
    }

    public Notification getNotificationbyReceiver(User receiver) {
        return this.notificationRepository.findByReceiver(receiver);
    }

    public Notification getNotificationbySender(User loggedInUser) {
        return this.notificationRepository.findBySender(loggedInUser);
    }

    public Notification getNotificationbySenderandReceiver(User sender,User receiver) {
        return this.notificationRepository.findBySenderAndReceiver(sender,receiver);
    }

    @Transactional
    public void save(Notification notification) {
        this.notificationRepository.save(notification);
    }



    @Transactional
    public  void deleteNotificationsByUser(Long userId) {
        this.notificationRepository.deleteBySenderIdOrReceiverId(userId,userId);
    }

    public void deleteFriendRequestNotification(User sender, User receiver) {
        Notification notification = notificationRepository.findBySenderAndReceiver(sender, receiver);
        if (notification != null) {
            notificationRepository.delete(notification);
        }
    }

}