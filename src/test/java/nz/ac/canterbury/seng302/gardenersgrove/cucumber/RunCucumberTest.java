package nz.ac.canterbury.seng302.gardenersgrove.cucumber;

import io.cucumber.junit.platform.engine.Constants;
import nz.ac.canterbury.seng302.gardenersgrove.GardenersGroveApplication;
import nz.ac.canterbury.seng302.gardenersgrove.service.ModerationService;
import org.junit.platform.suite.api.*;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("nz/ac/canterbury/seng302/gardenersgrove/cucumber")
@ConfigurationParameters({
        @ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "nz.ac.canterbury.seng302.gardenersgrove.cucumber"),
        @ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-report/cucumber.html"),
        @ConfigurationParameter(key = Constants.PLUGIN_PUBLISH_QUIET_PROPERTY_NAME, value = "true")
})
@ContextConfiguration(classes = GardenersGroveApplication.class)
@CucumberContextConfiguration
@SpringBootTest
@ActiveProfiles("cucumber")
@AutoConfigureMockMvc

// Permanent moderation api mock
@MockBean(ModerationService.class)

public class RunCucumberTest {

    @Autowired
    public RunCucumberTest(ModerationService moderationService)  {
        /*
         This constructor is run before every FEATURE, use it to set up mocks with their default behaviour.
         While the behaviour of the mocks can be adapted per test (see MockConfigurationSteps), creating the mocks
         initially should be done in this class, and their default behaviour configured here (see @MockBean above).

         Additionally, you can do other setup here that should be done the same for all tests, such as adding default
         users. If you want to get rid of any sample data in some cases, you can always write a Cucumber step to delete
         it, e.g., `Given no users already exist in the database` if you wanted to make sure there were no existing
         users for some particular feature.
        */

        // Mock successful moderation
        when(moderationService.moderateText(anyString())).thenReturn("null");

        when(moderationService.moderateText(eq("NotEvaluated"))).thenReturn("evaluation_error");

        // Mock unsuccessful moderation (profanity detected)
        when(moderationService.moderateText(eq("InappropriateTag"))).thenReturn("[{\"term\":\"InappropriateTerm\"}]");
    }
}
