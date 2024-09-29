package nz.ac.canterbury.seng302.gardenersgrove;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Image;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.ShopRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Gardener's Grove entry-point
 * Note @link{SpringBootApplication} annotation
 */
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableScheduling
public class GardenersGroveApplication {
	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private UserService userService;

	@Autowired
	private ImageService imageService;

	@Autowired
	private ItemService itemService;

	@Autowired
	private ShopService shopService;

	@Autowired
	private ShopRepository shopRepository;

	/**
	 * Main entry point, runs the Spring application
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(GardenersGroveApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner() throws IOException {
		Path defaultUserImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/defaultUserImage.png").getURI());
		byte[] defaultUserImageBytes = Files.readAllBytes(defaultUserImagePath);

		return args -> {
			createUserIfNotExists("startup@user.com", "Startup", "User", false, "01/01/2000", defaultUserImageBytes);
			createUserIfNotExists("sarah@email.com", "Sarah", "", true, "24/08/1987", defaultUserImageBytes);
			createUserIfNotExists("inaya@email.com", "Inaya", "Singh", false, "07/01/2000", defaultUserImageBytes);
			createUserIfNotExists("kaia@email.com", "Kaia", "Pene", false, "", defaultUserImageBytes);
			createUserIfNotExists("lei@email.com", "Lei", "Yuan", false, "27/02/1994", defaultUserImageBytes);
			createUserIfNotExists("liam@email.com", "Liam", "Müller", false, "", defaultUserImageBytes);
			createUserIfNotExists("gardenersgrove@email.com", "Gardeners Grove", "Inc", false, "", defaultUserImageBytes);

			// Create default items
			shopService.populateShopWithPredefinedItems();
		};
	}

	private void createUserIfNotExists(String email, String firstName, String lastName, boolean isAdmin, String dob, byte[] defaultUserImageBytes) {
		if (!userService.emailExists(email)) {
			Image image = new Image(defaultUserImageBytes, "png", false);
			User user = new User(firstName, lastName, isAdmin, email, "Password1!", dob, image);
			user.grantAuthority("ROLE_USER");
			userService.addUser(user);

			// Check if the user and the user's image are not null (For tests)
			if (user != null && user.getImage() != null && user.getUserId() != null) {
				// Retrieve the user from the database
				user = userService.getUserByID(user.getUserId());
				// init user with uploaded image id
				user.setUploadedImageId(user.getImage().getId());
				userService.saveUser(user);
			}
		}
	}
}
