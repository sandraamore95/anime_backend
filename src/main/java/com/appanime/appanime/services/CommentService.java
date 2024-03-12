package com.appanime.appanime.services;


import com.appanime.appanime.models.Comment;
import com.appanime.appanime.models.Notification;
import com.appanime.appanime.models.Post;
import com.appanime.appanime.models.User;
import com.appanime.appanime.repository.CommentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    CommentRepository commentRepository;


    public List<Comment> getAll() {
        return this.commentRepository.findAll();
    }

    public List<Comment> getAllbyUser(User user) {
        return this.commentRepository.findAllByAuthor(user);

    }

    public Optional<Comment> getCommentbyId(Long commentId) {
        return this.commentRepository.findById(commentId);

    }

    public List<Comment> getCommentsByPost(Optional<Post> post) {
        // Implementación para obtener todos los comentarios asociados a un post específico
        return commentRepository.findByPostId(post.get().getId());
    }

    public Comment save(Comment comment) {
        return this.commentRepository.save(comment);
    }

    //elimina el comentario en general
    public void eliminarComment(Comment comment) {
        this.commentRepository.delete(comment);
    }

    public List<Comment> getAllbyPost(Long postId) {
        return this.commentRepository.findAllByPostId(postId);
    }


    public List<Comment> getCommentsByUserorPostAuthor(Long userId) {
        return commentRepository.findByAuthorOrPostAuthor(userId);
    }
}
