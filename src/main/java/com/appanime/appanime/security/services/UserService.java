package com.appanime.appanime.security.services;

import com.appanime.appanime.models.User;
import com.appanime.appanime.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public interface UserService {
    List<User> getAllUsers();

    boolean changePassword(User user, String newPassword);

    boolean changeEmail(User user,String newEmail);

    User getUserById(long id);
    User getUserByUsername(String username);
    void deleteUser(User user);


    User getUserByEmail(String email);

    void save(User user);
}
