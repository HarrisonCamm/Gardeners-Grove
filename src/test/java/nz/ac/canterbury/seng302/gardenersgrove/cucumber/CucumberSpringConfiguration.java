package nz.ac.canterbury.seng302.gardenersgrove.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import nz.ac.canterbury.seng302.gardenersgrove.GardenersGroveApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(classes = GardenersGroveApplication.class)
@ActiveProfiles("cucumber")
public class CucumberSpringConfiguration {
}
