package nz.ac.canterbury.seng302.gardenersgrove;

import nz.ac.canterbury.seng302.gardenersgrove.entity.BadgeItem;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Image;
import nz.ac.canterbury.seng302.gardenersgrove.entity.ImageItem;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.ImageService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ItemService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.VerificationTokenService;
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
	private VerificationTokenService verificationTokenService;

	@Autowired
	private ImageService imageService;

	@Autowired
	private ItemService itemService;

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
			// Check if the user already exists
			if (!userService.emailExists("startup@user.com")) {
				Image image = new Image(defaultUserImageBytes, "png", false);
				User user = new User("Startup", "User", false, "startup@user.com", "Password1!", "01/01/2000", image);
				user.grantAuthority("ROLE_USER");
				userService.addUser(user);
			}
			if (!userService.emailExists("sarah@email.com")) {
				Image image = new Image(defaultUserImageBytes, "png", false);
				User user = new User("Sarah", "", true, "sarah@email.com", "Password1!", "24/08/1987", image);
				user.grantAuthority("ROLE_USER");
				userService.addUser(user);
			}
			if (!userService.emailExists("inaya@email.com")) {
				Image image = new Image(defaultUserImageBytes, "png", false);
				User user = new User("Inaya", "Singh", false, "inaya@email.com", "Password1!", "07/01/2000", image);
				user.grantAuthority("ROLE_USER");
				userService.addUser(user);
			}
			if (!userService.emailExists("kaia@email.com")) {
				Image image = new Image(defaultUserImageBytes, "png", false);
				User user = new User("Kaia", "Pene", false, "kaia@email.com", "Password1!", "", image);
				user.grantAuthority("ROLE_USER");
				userService.addUser(user);
			}
			if (!userService.emailExists("lei@email.com")) {
				Image image = new Image(defaultUserImageBytes, "png", false);
				User user = new User("Lei", "Yuan", false, "lei@email.com", "Password1!", "27/02/1994", image);
				user.grantAuthority("ROLE_USER");
				userService.addUser(user);
			}
			if (!userService.emailExists("liam@email.com")) {
				Image image = new Image(defaultUserImageBytes, "png", false);
				User user = new User("Liam", "Müller", false, "liam@email.com", "Password1!", "", image);
				user.grantAuthority("ROLE_USER");
				userService.addUser(user);
			}
			if (!userService.emailExists("gardenersgrove@email.com")) {
				Image image = new Image(defaultUserImageBytes, "png", false);
				User user = new User("Gardeners Grove", "Inc", false, "gardenersgrove@email.com", "Password1!", "", image);
				user.grantAuthority("ROLE_USER");
				userService.addUser(user);
			}

//			// Create default items
//			if (!itemService.itemExists("Cat Fall")) {
//				Path catFallImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/cat-fall.gif").getURI());
//				byte[] catFallImageBytes = Files.readAllBytes(catFallImagePath);
//				Image image = new Image(catFallImageBytes, "gif", false);
//				ImageItem imageItem = new ImageItem("Cat Fall",5000, image);
//				itemService.saveItem(imageItem);
//			}
//			if (!itemService.itemExists("Cat Typing")) {
//				Path catTypingImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/cat-typing.gif").getURI());
//				byte[] catTypingImageBytes = Files.readAllBytes(catTypingImagePath);
//				Image image = new Image(catTypingImageBytes, "gif", false);
//				ImageItem imageItem = new ImageItem("Cat Typing",3000, image);
//				itemService.saveItem(imageItem);
//			}
//			if (!itemService.itemExists("Starfruit")) {
//				Path starfruitImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/starfruit.jpg").getURI());
//				byte[] starfruitImageBytes = Files.readAllBytes(starfruitImagePath);
//				Image image = new Image(starfruitImageBytes, "jpg", false);
//				ImageItem imageItem = new ImageItem("Starfruit", 1000, image);
//				itemService.saveItem(imageItem);
//			}
//			if (!itemService.itemExists("Nerd")) {
//				BadgeItem badgeItem = new BadgeItem("Nerd", false, 750, "\uD83E\uDD13");
//				itemService.saveItem(badgeItem);
//
//			}
//			if (!itemService.itemExists("Chestnut")) {
//				BadgeItem badgeItem = new BadgeItem("Chestnut", false, 250, "\uD83C\uDF30");
//				itemService.saveItem(badgeItem);
//			}
		};
	}
}