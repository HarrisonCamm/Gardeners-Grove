package nz.ac.canterbury.seng302.gardenersgrove.cucumber;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.context.SpringBootTest;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("nz/ac/canterbury/seng302/gardenersgrove/cucumber")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME,value = "nz/ac/canterbury/seng302/gardenersgrove/cucumber/step_definitions")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME,value = "pretty, html:target/cucumber-report/cucumber.html")
@WebMvcTest
public class RunCucumberIntegrationTest {
}
