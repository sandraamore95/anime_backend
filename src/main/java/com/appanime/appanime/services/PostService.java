package com.appanime.appanime.services;

import com.appanime.appanime.models.Notification;
import com.appanime.appanime.models.Post;
import com.appanime.appanime.models.User;
import com.appanime.appanime.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    PostRepository postRepository;

    public Post save(Post post) {
        return  this.postRepository.save(post);
    }


    public List<Post> getAllPostsbyUser(Long userId) {
        return this.postRepository.findAllByAuthorId(userId);
    }

    public Optional<Post> getPostbyId(Long postId) {
        return this.postRepository.findById(postId);
    }

    public void eliminarPost(Post post) {
        this.postRepository.delete(post);
    }

    public Page<Post> getAllByUser(User user, Pageable pageable) {
        return postRepository.findByAuthor(user, pageable);
    }
    public User getUserByPostId(Long postId) {
        // Lógica para buscar y obtener el autor del post a partir del ID
        // Aquí podrías utilizar tu repositorio o lógica de acceso a la base de datos
        // Supongamos que tienes un método en tu repositorio llamado findAuthorByPostId
        return postRepository.findAuthorByPostId(postId);
    }

    @Transactional
    public void deletePostsByUser(Long id) {
        this.postRepository.deleteAllByAuthorId(id);
    }
}
