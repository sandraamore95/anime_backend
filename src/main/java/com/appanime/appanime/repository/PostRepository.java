package com.appanime.appanime.repository;


import com.appanime.appanime.models.Notification;
import com.appanime.appanime.models.Post;
import com.appanime.appanime.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {




    Page<Post> findByAuthor(User user, Pageable pageable);
    @Query("SELECT p.author FROM Post p WHERE p.id = :postId")
    User findAuthorByPostId(@Param("postId") Long postId);

    void deleteAllByAuthorId(Long id);

    List<Post> findAllByAuthorId(Long userId);
}
