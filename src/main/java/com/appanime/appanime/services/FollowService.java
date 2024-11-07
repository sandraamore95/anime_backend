package com.appanime.appanime.services;
import com.appanime.appanime.models.Follow;
import com.appanime.appanime.models.User;
import com.appanime.appanime.repository.FollowRepository;
import com.appanime.appanime.security.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class FollowService {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserService userService;


    public void followUser(User follower, User followed) {
        if (!isFollowing(follower, followed)) {
            Follow follow = new Follow();
            follow.setFollower(follower);
            follow.setFollowed(followed);
            followRepository.save(follow);
        }
    }


    public void unfollowUser(User follower, User followed) {
        Follow follow = followRepository.findByFollowerAndFollowed(follower, followed);
        if (follow != null) {
            followRepository.delete(follow);
        }
    }


    public boolean isFollowing(User follower, User followed) {
        return followRepository.existsByFollowerAndFollowed(follower, followed);
    }


    public List<User> getFollowing(User follower) {
        List<Follow> follows = followRepository.findByFollower(follower);
        return follows.stream().map(Follow::getFollowed).collect(Collectors.toList());
    }


    public List<User> getFollowers(User followed) {
        List<Follow> followers = followRepository.findByFollowed(followed);
        return followers.stream().map(Follow::getFollower).collect(Collectors.toList());
    }
    @Transactional
    public void deleteFollowsAndRelatedRequestsByUserId(Long userId) {
        // Eliminar relaciones de seguimiento donde el usuario es follower o followed
        followRepository.deleteByFollowerIdOrFollowedId(userId, userId);

    }


}

