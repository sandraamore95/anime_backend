package com.appanime.appanime.services;

import com.appanime.appanime.models.Profile;
import com.appanime.appanime.models.User;
import com.appanime.appanime.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    @Autowired
    ProfileRepository profileRepository;

    public void saveProfile(Profile profile) {
        this.profileRepository.save(profile);

    }

    public Profile getProfileByUser(User user) {
        return this.profileRepository.findAllByUser(user);
    }

    public void eliminarProfile(Profile profile) {
      this.profileRepository.delete(profile);
    }
}
