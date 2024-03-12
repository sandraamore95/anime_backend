package com.appanime.appanime.services;

import com.appanime.appanime.models.FavoritePost;
import com.appanime.appanime.models.Post;
import com.appanime.appanime.models.User;
import com.appanime.appanime.repository.FavoritePostsRepository;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service



public class FavoritePostsService {

    @Autowired
    FavoritePostsRepository favoritePostsRepository;
    public List<FavoritePost> getFavoritePostByUser(User user){
      return   this.favoritePostsRepository.findAllByUser(user);
    }

    public List<FavoritePost> getFavoritePostsByPost(Long postId) {
        return this.favoritePostsRepository.findAllByPostId(postId);
    }

    public void eliminarFavoritePost(FavoritePost favoritePost) {
        this.favoritePostsRepository.delete(favoritePost);
    }

    public void saveFavoritePost(FavoritePost favoritePost) {
        this.favoritePostsRepository.save(favoritePost);
    }


    public FavoritePost getFavoritePostByUserAndPost(Long postId, User user) {
        FavoritePost favoritePost = (FavoritePost) this.favoritePostsRepository.findByPostIdAndUserId(postId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Favorite not found"));
            return favoritePost;
    }




    public boolean isFavorite(Long postId, User user) {
        return this.favoritePostsRepository.findByPostIdAndUserId(postId, user.getId()).isPresent();
    }


}
