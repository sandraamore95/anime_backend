package com.appanime.appanime.repository;

import com.appanime.appanime.models.Friendship;
import com.appanime.appanime.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    List<Friendship> findAllByUser(User user);


    // Ejemplo de método para encontrar amistades específicas entre dos usuarios
    Optional<Friendship> findByUserAndFriend(User user, User friend);
    void deleteByUserAndFriend(User user, User friend);

    @Query("SELECT COUNT(f) > 0 FROM Friendship f " +
            "WHERE (f.user = :sender AND f.friend = :receiver) OR " +
            "(f.user = :receiver AND f.friend = :sender)")
    boolean areFriends(@Param("sender") User sender, @Param("receiver") User receiver);

    boolean existsByUserAndFriend(User user, User friend);

    void deleteByUserIdOrFriendId(Long userId, Long friendId);
}
