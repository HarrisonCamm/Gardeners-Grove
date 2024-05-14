package nz.ac.canterbury.seng302.gardenersgrove;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Image;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.ImageService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Gardener's Grove entry-point
 * Note @link{SpringBootApplication} annotation
 */
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class GardenersGroveApplication {
	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private UserService userService;
	private VerificationTokenService verificationTokenService;

	@Autowired
	private ImageService imageService;

	/**
	 * Main entry point, runs the Spring application
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(GardenersGroveApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner() throws IOException {
		Path path = Paths.get(resourceLoader.getResource("classpath:static/images/defaultUserImage.png").getURI());
		byte[] imageBytes = Files.readAllBytes(path);

		return args -> {
			// Check if the user already exists
			if (!userService.emailExists("startup@user.com")) {
				User user = new User("Startup", "User", false, "startup@user.com", "password", "01/01/2000");
				Image image = new Image(imageBytes, "png", false);
				user.setImage(image);
				user.grantAuthority("ROLE_USER");
				userService.addUser(user);
			}
			if (!userService.emailExists("sarah@email.com")) {
				User user = new User("Sarah", "", true, "sarah@email.com", "password123", "24/08/1987");
				Image image = new Image(imageBytes, "png", false);
				user.setImage(image);
				user.grantAuthority("ROLE_USER");
				userService.addUser(user);
			}
			if (!userService.emailExists("inaya@email.com")) {
				User user = new User("Inaya", "Singh", false, "inaya@email.com", "password123", "07/01/2000");
				Image image = new Image(imageBytes, "png", false);
				user.setImage(image);
				user.grantAuthority("ROLE_USER");
				userService.addUser(user);
			}
			if (!userService.emailExists("kaia@email.com")) {
				User user = new User("Kaia", "Pene", false, "kaia@email.com", "password123", "");
				Image image = new Image(imageBytes, "png", false);
				user.setImage(image);
				user.grantAuthority("ROLE_USER");
				userService.addUser(user);
			}
			if (!userService.emailExists("lei@email.com")) {
				User user = new User("Lei", "Yuan", false, "lei@email.com", "password123", "27/02/1994");
				Image image = new Image(imageBytes, "png", false);
				user.setImage(image);
				user.grantAuthority("ROLE_USER");
				userService.addUser(user);
			}
			if (!userService.emailExists("liam@email.com")) {
				User user = new User("Liam", "Müller", false, "liam@email.com", "password123", "");
				Image image = new Image(imageBytes, "png", false);
				user.setImage(image);
				user.grantAuthority("ROLE_USER");
				userService.addUser(user);
			}
		};
	}
}