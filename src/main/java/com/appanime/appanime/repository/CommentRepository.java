package com.appanime.appanime.repository;

import com.appanime.appanime.models.Comment;
import com.appanime.appanime.models.Friendship;
import com.appanime.appanime.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository  extends JpaRepository<Comment, Long> {
    List<Comment> findAllByAuthor(User user);

    List<Comment> findAllByPostId(Long postId);

    List<Comment> findByPostId(Long id);

    void deleteAllByAuthorId(Long id);

    @Query("SELECT c FROM Comment c WHERE c.author.id = ?1 OR c.post.author.id = ?1")
    List<Comment> findByAuthorOrPostAuthor(Long userId);
}
