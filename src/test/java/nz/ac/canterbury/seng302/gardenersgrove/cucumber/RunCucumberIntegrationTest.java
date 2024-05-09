package nz.ac.canterbury.seng302.gardenersgrove.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(Cucumber.class)
@CucumberOptions(features = "nz/ac/canterbury/seng302/gardenersgrove/cucumber")
@CucumberContextConfiguration
@SpringBootTest
public class RunCucumberIntegrationTest {
}
