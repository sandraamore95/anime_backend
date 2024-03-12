package com.appanime.appanime.repository;

import com.appanime.appanime.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationCommentRepository extends JpaRepository<Notification.CommentNotification, Long> {

}
