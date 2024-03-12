package com.appanime.appanime.security.services;

import com.appanime.appanime.models.User;
import com.appanime.appanime.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;


    @Override
    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    @Override
    public boolean changePassword(User user, String newPassword) {

        //tiene q dar error si la nueva contraseña coincide con la actual del usuario
        if (user != null && (!passwordEncoder.matches(newPassword, user.getPassword()))) {

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true; // Éxito en cambiar la contraseña
        } else {

            return false; // Falló al cambiar la contraseña
        }
    }


    @Override
    public boolean changeEmail(User user, String newEmail) {
      Boolean user_exist=  userRepository.existsByEmail(newEmail) || (user.getEmail().equals(newEmail)) ;
        if (user != null && (!user_exist)) {
                user.setEmail(newEmail);
            userRepository.save(user);
                return true; // Éxito al cambiar el email
        } else {
            return false; // Falló al cambiar el email
        }
    }

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Override
    public User getUserById(long id) {
        Optional<User> optional = userRepository.findById(id);
        User user = null;
        if (optional.isPresent()) {
            user = optional.get();
        } else {
            throw new RuntimeException(" User not found for id :: " + id);
        }
        return user;
    }

    public User getUserByUsername(String username){
        Optional<User> optional = userRepository.findByUsername(username);
        User user = null;
        if (optional.isPresent()) {
            user = optional.get();
        } else {
            throw new RuntimeException(" User not found for id :: " + username);
        }
        return user;
    }
    public User getUserByEmail(String email){
        Optional<User> optional = userRepository.findByEmail(email);
        User user = null;
        if (optional.isPresent()) {
            user = optional.get();
        } else {
            throw new RuntimeException(" User not found for email :: " + email);
        }
        return user;
    }

    @Override
    public void save(User user) {
        this.userRepository.save(user);
    }


    @Override
    public void deleteUser(User user){
        this.userRepository.delete(user);
    }






}
