package com.appanime.appanime.repository;


import com.appanime.appanime.models.Profile;
import com.appanime.appanime.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {


    Profile findAllByUser(User user);

    void deleteByUserId(Long id);
}
