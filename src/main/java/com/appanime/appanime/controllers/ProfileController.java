package com.appanime.appanime.controllers;

import com.appanime.appanime.models.Profile;
import com.appanime.appanime.models.User;
import com.appanime.appanime.security.services.UserService;
import com.appanime.appanime.services.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.lang.reflect.Field;
import java.security.Principal;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService; // Supongamos que tienes un servicio para gestionar los perfiles

    @Autowired
    private UserService userService;
    @PostMapping("/add-profile")
    public ResponseEntity<String> addProfile(@RequestBody User user) {

        try {
            // Verificar si el usuario existe
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }
            System.out.println(user.getId());
            System.out.println(user.getUsername());

            // Crear una nueva instancia de Profile
            Profile profile = new Profile();
            profile.setUser(user);
            profile.setUsername(user.getUsername());
            profile.setAbout("Bienvenid@s a mi Perfil ! :)");

            // Guardar el perfil en la base de datos
            profileService.saveProfile(profile);


            return ResponseEntity.ok("Perfil creado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el perfiiiiiiil");
        }
    }
    @GetMapping("/get-profile/{username}")
    public ResponseEntity<Profile> getProfile(@PathVariable String username) {
        try {
            // Obtener el nombre de usuario del usuario autenticado
            // Obtener el perfil asociado al usuario
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            Profile profile = profileService.getProfileByUser(user);
            if (profile == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            System.out.println(profile.getUsername());
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



    @PutMapping("/update-profile")
    public ResponseEntity<Profile> updateProfile(@RequestBody Profile profileUpdate, Principal principal) {
        try {
            // Obtener el nombre de usuario del usuario autenticado
            String username = principal.getName();

            // Obtener el usuario asociado al perfil
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            Profile existingProfile = this.profileService.getProfileByUser(user);

            // Obtener todos los campos declarados en la clase Profile
            Field[] fields = Profile.class.getDeclaredFields();

            // Iterar sobre los campos
            for (Field field : fields) {
                // Acceder a cada campo
                field.setAccessible(true);

                // Obtener el nuevo valor del campo desde el objeto recibido
                Object updatedValue = field.get(profileUpdate);

                // Actualizar solo si el nuevo valor no es nulo
                if (updatedValue != null) {
                    field.set(existingProfile, updatedValue);
                }
            }

            // Guardar los cambios en la base de datos
            profileService.saveProfile(existingProfile);

            return ResponseEntity.ok(existingProfile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }





}




