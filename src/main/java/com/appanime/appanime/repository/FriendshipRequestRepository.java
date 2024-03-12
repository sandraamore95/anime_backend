package com.appanime.appanime.repository;

import com.appanime.appanime.models.Friendship;
import com.appanime.appanime.models.User;
import com.appanime.appanime.payload.request.FriendshipRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface FriendshipRequestRepository extends JpaRepository<FriendshipRequest, Long> {
    Optional<FriendshipRequest> findBySenderAndReceiver(User sender, User receiver);

    Optional<FriendshipRequest> findByReceiverAndSender(User receiver, User sender);

    FriendshipRequest findByReceiver(User receiver);

    List<FriendshipRequest> findAllByReceiver(User receiver);

    boolean existsBySenderAndReceiver(User sender, User receiver);

    List findBySender(User receiver);

    void deleteBySenderIdOrReceiverId(Long userId, Long userId1);
}
