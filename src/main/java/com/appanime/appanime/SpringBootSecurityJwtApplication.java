package com.appanime.appanime;
import com.appanime.appanime.models.ERole;
import com.appanime.appanime.models.Role;
import com.appanime.appanime.models.User;
import com.appanime.appanime.repository.RoleRepository;
import com.appanime.appanime.repository.UserRepository;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import static com.appanime.appanime.controllers.UserController.saveImageForUser;
import org.springframework.core.SpringVersion;

@SpringBootApplication
public class SpringBootSecurityJwtApplication {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository rolRepository;
	public static void main(String[] args) {

		System.out.println("Spring Version:" + SpringVersion.getVersion());
		SpringApplication.run(SpringBootSecurityJwtApplication.class, args);
	}
	@PostConstruct
/*
public void init() {
		System.out.println("estamos aqui!");
		Role adminRol = new Role();
		adminRol.setName(ERole.ROLE_ADMIN);
		rolRepository.save(adminRol);

		Role userRol = new Role();
		userRol.setName(ERole.ROLE_USER);
		rolRepository.save(userRol);

		User admin = new User();
		admin.setUsername("admin1");
		admin.setEmail("admin1@example.com");



		admin.setPassword(encryptPassw().encode("admin123"));
		System.out.println(admin.getPassword());
		admin.getRoles().add(adminRol);
		userRepository.save(admin);

		User user = new User();
		user.setUsername("user1");
		user.setEmail("user1@example.com");
		saveImageForUser("https://www.der-windows-papst.de/wp-content/uploads/2019/03/Windows-Change-Default-Avatar-448x400.png","user1");

		user.setPassword(encryptPassw().encode("user123"));
		System.out.println(user.getPassword());
		user.getRoles().add(userRol);
		userRepository.save(user);
		// hay que agregar un profile para este usuario


	}
*/
	public PasswordEncoder encryptPassw() {
		return new BCryptPasswordEncoder();
	}



}
