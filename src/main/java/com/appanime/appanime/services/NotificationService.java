package com.appanime.appanime.services;

import com.appanime.appanime.models.Notification;
import com.appanime.appanime.models.User;
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
    public Notification.CommentNotification getCommentNotification(Long id_notification) {
        Optional<Notification> optionalNotification = notificationRepository.findById(id_notification);

        // Verificar si la notificación se encuentra
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            if (notification instanceof Notification.CommentNotification) {
                return (Notification.CommentNotification) notification;
            } else {
                // Manejar el caso donde la notificación no es una CommentNotification
                return null;
            }
        } else {
            // Manejar el caso donde la notificación no se encuentra
            // Puedes lanzar una excepción, devolver null o manejarlo de alguna otra forma según tus requerimientos.
            // Aquí simplemente devolvemos null.
            return null;
        }
    }
    public List<Notification> findRelatedNotifications(User sender, User receiver) {
        return notificationRepository.findRelatedNotifications(sender,receiver);
    }

    public List <Notification> findNotifications(User sender, User receiver){
        return this.notificationRepository.findBySenderOrReceiver(sender,receiver);
    }

    public void eliminar( Notification notification){
        this.notificationRepository.delete(notification);

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

    //ELIMINAR NOTIFICACION DE TIPO REQUEST
    public void deleteFriendRequestNotification(User sender, User receiver) {
        Notification notification = notificationRepository.findBySenderAndReceiver(sender, receiver);
        if (notification != null) {
            System.out.println("estamos en lak noti");
            System.out.println(notification.getId());
            notificationRepository.delete(notification);
        }
    }


}