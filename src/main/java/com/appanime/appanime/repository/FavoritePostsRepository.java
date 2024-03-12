package com.appanime.appanime.repository;

import com.appanime.appanime.models.FavoritePost;
import com.appanime.appanime.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritePostsRepository extends JpaRepository<FavoritePost, Long> {


    List<FavoritePost> findAllByUser(User user);

    List<FavoritePost> findAllByPostId(Long existPost);

    Optional<Object> findByPostIdAndUserId(Long postId, Long id);
}
