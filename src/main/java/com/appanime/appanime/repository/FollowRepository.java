package com.appanime.appanime.repository;
import com.appanime.appanime.models.Follow;
import com.appanime.appanime.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerAndFollowed(User follower, User followed);
    Follow findByFollowerAndFollowed(User follower, User followed);
    List<Follow> findByFollower(User follower);
    List<Follow> findByFollowed(User followed);

    void deleteByFollowerIdOrFollowedId(Long userId, Long userId1);
}
