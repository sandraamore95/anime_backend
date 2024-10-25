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
        return this.notificationRepository.findAll();
    }

    public List<Notification> getAllByUserFollowed(User followed) {
        return this.notificationRepository.findAllByFollowed(followed);
    }

    public List<Notification> getAllByUserFollower(User follower) {
        return this.notificationRepository.findAllByFollower(follower);
    }

    public Optional<Notification> getNotificationById(Long idNotification) {
        return this.notificationRepository.findById(idNotification);
    }

    public Notification.CommentNotification getCommentNotification(Long idNotification) {
        Optional<Notification> optionalNotification = notificationRepository.findById(idNotification);

        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            if (notification instanceof Notification.CommentNotification) {
                return (Notification.CommentNotification) notification;
            } else {
                return null; // Manejo de casos donde no es una CommentNotification
            }
        } else {
            return null; // Manejo de casos donde no se encuentra la notificación
        }
    }

    public List<Notification> findRelatedNotifications(User follower, User followed) {
        return notificationRepository.findRelatedNotifications(follower, followed);
    }

    @Transactional
    public void eliminar(Notification notification) {
        this.notificationRepository.delete(notification);
    }

    @Transactional
    public void actualizar(Notification notification) {
        notificationRepository.save(notification);
    }

    public Notification getNotificationByFollowerAndFollowed(User follower, User followed) {
        return this.notificationRepository.findByFollowedAndFollower(followed, follower);
    }

    @Transactional
    public void save(Notification notification) {
        this.notificationRepository.save(notification);
    }

    @Transactional
    public void deleteNotificationsByUser(Long userId) {
        this.notificationRepository.deleteByFollowerIdOrFollowedId(userId, userId);
    }

    // ELIMINAR NOTIFICACION DE TIPO COMMENT
    public void deleteCommentNotification(User follower, User followed) {
        Notification notification = notificationRepository.findByFollowerAndFollowed(follower, followed);
        if (notification != null) {
            notificationRepository.delete(notification);
        }
    }


}