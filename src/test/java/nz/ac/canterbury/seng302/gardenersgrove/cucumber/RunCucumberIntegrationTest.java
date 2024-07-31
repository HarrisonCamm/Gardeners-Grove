package nz.ac.canterbury.seng302.gardenersgrove.cucumber;

import io.cucumber.junit.platform.engine.Constants;
import nz.ac.canterbury.seng302.gardenersgrove.GardenersGroveApplication;
import nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions.AddTagSteps;
import org.junit.platform.suite.api.*;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("nz/ac/canterbury/seng302/gardenersgrove/cucumber")
@ConfigurationParameters({
        @ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "nz.ac.canterbury.seng302.gardenersgrove.cucumber"),
        @ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-report/cucumber.html")
})
@ContextConfiguration(classes = CucumberSpringConfiguration.class)
@SpringBootTest
@ActiveProfiles("cucumber")
@AutoConfigureMockMvc
public class RunCucumberIntegrationTest {
}
