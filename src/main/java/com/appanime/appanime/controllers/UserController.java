package com.appanime.appanime.controllers;
import com.appanime.appanime.models.User;
import com.appanime.appanime.security.services.UserService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")


public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public List<User> getAll() {
        return this.userService.getAllUsers();
    }


    //USER by id

    //USER by id
    @GetMapping("/id/{userId}")
    public User getUserbyId(@PathVariable Long userId) {
        return this.userService.getUserById(userId);

    }

    //USER by username
    @GetMapping("/user/{username}")
    public User getUserByUsername(@PathVariable String username){
        return  this.userService.getUserByUsername(username);
    }

    //DELETE by id
    @DeleteMapping(path = "/{id}")
    public Map<String, Boolean> deleteUser(@PathVariable(value = "id") Long user_id)
            throws Exception {
        User user = this.userService.getUserById(user_id);
        this.userService.deleteUser(user);
        Map<String, Boolean> response = new HashMap<>();
        response.put("User deleted!", Boolean.TRUE);
        return response;
    }

    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public String userAccess() {
        return "User Content.";
    }

    @GetMapping("/mod")
    @PreAuthorize("hasRole('MODERATOR')")
    public String moderatorAccess() {
        return "Moderator Board.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }


    //UPDATE PASSWORD
    @GetMapping("/change-password/{newPassw}")
    public ResponseEntity<String> changePassword(@PathVariable String newPassw, Principal principal) {
        String username = principal.getName();
        boolean success = userService.changePassword(getCurrentUser(username),newPassw);
        if (success) {
            return ResponseEntity.ok("Password changed successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to change password");
        }
    }

    //UPDATE EMAIL
    @GetMapping("/change-email/{newEmail}")
    public ResponseEntity<String> changeEmail(@PathVariable String newEmail, Principal principal) {
        String username = principal.getName();
        boolean success = userService.changeEmail(getCurrentUser(username) ,newEmail);
        if (success) {
            return ResponseEntity.ok("Email changed successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to change email");
        }
    }

    //GET PROFILE-IMAGE
    @GetMapping("/profile-image/{username}")
    public ResponseEntity<byte[]> getAvatarImage(@PathVariable String username) throws IOException {
        User user =getCurrentUser(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        Path path = Paths.get("./images/avatar", username, "/avatar.png");
        System.out.println(path);
        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }
        byte[] image = Files.readAllBytes(path);
        return ResponseEntity.ok(image);
    }

    //UPDATE PROFILE-IMAGE
    @PostMapping(value = "/update-profile-image", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> updateProfileImage(Principal principal, @RequestParam("file") MultipartFile file) {
        String username = principal.getName();
        User user = getCurrentUser(username);
        if (!file.isEmpty()) {
            try {
                // Verificar si el archivo es una imagen válida con extensiones .png, .jpeg, .jpg
                String[] allowedFiles = {"image/jpeg", "image/png", "image/webp"};
                if (Arrays.asList(allowedFiles).contains(file.getContentType())) {
                    Path directory = Paths.get("./images/avatar/" + username);
                    if (Files.notExists(directory)) {
                        Files.createDirectories(directory);
                    }
                    Path imagePath = directory.resolve("avatar.png");
                    Files.copy(file.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);

                    // Leer los bytes de la imagen
                    byte[] imageBytes = Files.readAllBytes(imagePath);

                    // Devolver los bytes de la imagen en la respuesta
                    return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(imageBytes);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("El formato del archivo no es correcto.".getBytes());
                }

            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Error uploading image.".getBytes());
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Empty file.".getBytes());
        }
    }

    private User getCurrentUser(String username){
        return  this.userService.getUserByUsername(username);
    }


    public static void saveImageForUser(String imageUrl, String username) {
        String directoryPath = "./images/avatar/";
        try {
            URL url = new URL(imageUrl);
            String fileName = "avatar.png"; // Nombre del archivo
            Path directory = Paths.get(directoryPath, username);

            if (Files.notExists(directory)) {
                Files.createDirectories(directory);
            }

            Path imagePath = directory.resolve(fileName);
            try (InputStream in = url.openStream();
                 OutputStream out = new FileOutputStream(imagePath.toFile())) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            System.out.println("Imagen descargada y almacenada en: " + imagePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
























