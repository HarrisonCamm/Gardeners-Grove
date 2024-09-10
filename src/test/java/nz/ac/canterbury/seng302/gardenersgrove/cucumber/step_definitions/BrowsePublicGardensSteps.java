package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import com.zaxxer.hikari.SQLExceptionOverride;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.LocationService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.hamcrest.Matchers;
import org.mockito.internal.matchers.Equals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.stream.IntStream;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class BrowsePublicGardensSteps {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private GardenService gardenService;

    @Autowired
    private UserService userService;

    @Autowired
    private PlantService plantService;

    private MockMvc mockMvc;

    private Garden publicGarden;

    private User gardenOwner;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Before("@SingleGarden")
    public void setupSingleGarden() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Create a test garden owner
        User gardenOwner = new User(
                "inaya@email.com",  // Email
                "Inaya",                    // First name
                true,                       // No last name
                "",                         // Empty last name
                "Password1!",               // Password
                "1990-01-01"                // Date of birth
        );

        // Save user to a database
        userService.addUser(gardenOwner);

        // Create test location
        Location location = new Location("Test Street", "Test Suburb", "Test City", "1234", "Country");

        // Create a test garden
        publicGarden = new Garden("Public Test Garden", location, "100", gardenOwner, "A public garden");

        // Mark the garden as public
        publicGarden.setIsPublic(true);

        // Save garden to a database
        gardenService.addGarden(publicGarden);

        // Create and add plants to the garden
        Plant testPlant = new Plant(publicGarden, "TestPlant", "5", "Testy plant", "2024-09-05");

        // Save plants to the database
        plantService.addPlant(testPlant);

        // Save garden to a database
        gardenService.addGarden(publicGarden);
    }

    @Before("@MultipleGardens")
    public void setupMultipleGardens() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Create a test garden owner
        User gardenOwner = new User(
                "inaya@email.com",  // Email
                "Inaya",                    // First name
                true,                       // No last name
                "",                         // Empty last name
                "Password1!",               // Password
                "1990-01-01"                // Date of birth
        );

        // Save user to a database
        userService.addUser(gardenOwner);

        // Use 19 as already 1 in database
        IntStream.rangeClosed(1, 19).forEach(i -> {
            // NOTE: If not done, get a detached entity problem.
            // NOTE: Tried persisting location to database did not fix
            // Create a new unique location for each garden
            Location location = new Location("Test Street " + i, "Test Suburb", "Test City", "1234", "Country");

            String gardenName = "Public Test Garden " + i;
            Garden garden = new Garden(gardenName, location, "100", gardenOwner, "A public garden " + i);

            // Mark the garden as public
            garden.setIsPublic(true);

            // Save garden to a database
            gardenService.addGarden(garden);

            // Create and add plants to the garden
            Plant testPlant = new Plant(garden, "TestPlant " + i, "5", "Testy plant " + i, "2024-09-05");

            // Save plants to the database
            plantService.addPlant(testPlant);
        });
    }

    // AC1
    @Given("a garden has been marked as public")
    public void gardenMarkedAsPublic() {
        // Set garden public
        publicGarden.setIsPublic(true);

        // Save garden change to a database
        gardenService.addGarden(publicGarden);
    }

    // AC1
    @Then("any logged-in user can view the name, size, and plants when clicking on a link to the garden")
    public void loggedInUserCanViewGardenDetails() throws Exception {
        // Create the public garden link
        String gardenLink = "/view-garden?gardenID=" + publicGarden.getId();

        // Perform the GET request to view that garden
        mockMvc.perform(get(gardenLink))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("gardenName", "gardenSize", "plants"))
                .andExpect(model().attribute("gardenName", publicGarden.getName()))
                .andExpect(model().attribute("gardenSize", publicGarden.getSize()))
                // Ensure there's exactly one plant
                .andExpect(model().attribute("plants", hasSize(1)))
                // Check the properties of the plant
                .andExpect(model().attribute("plants", hasItem(
                        allOf(
                                hasProperty("name", is("TestPlant")),
                                hasProperty("count", is("5")),
                                hasProperty("description", is("Testy plant")),
                                hasProperty("datePlanted", is("2024-09-05"))
                        )
                )));
    }

    // AC2
    @Given("I am anywhere on the system")
    public void iAmAnywhereOnTheSystem() {
        // Assuming user is logged in and navigating the system
    }

    @When("I click the {string} button on the navigation bar")
    public void iClickTheButtonOnTheNavigationBar(String browserGardens) throws Exception {
        // change button name to actual url
        browserGardens = "/browse-gardens";

        // Click the browse gardens button
        mockMvc.perform(get(browserGardens).with(csrf()))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("gardenPage"));
    }

    // AC2
    @Then("I am taken to a page with a search text box and the {int} or fewer of newest created gardens")
    public void iAmTakenToAPageWithASearchTextBoxAndTheOrFewerOfNewestCreatedGardens(Integer maxGardens) throws Exception {
        // Perform the GET request to the "/browse-gardens" endpoint
        mockMvc.perform(get("/browse-gardens"))
                .andExpect(status().isOk())
                // Ensure the gardenPage attribute exists
                .andExpect(MockMvcResultMatchers.model().attributeExists("gardenPage"))

                // Ensure at least 1 garden is present
                .andExpect(MockMvcResultMatchers.model().attribute("gardenPage", hasProperty("content", hasSize(greaterThanOrEqualTo(1)))))

                // Ensure no more than maxGardens are displayed
                .andExpect(MockMvcResultMatchers.model().attribute("gardenPage", hasProperty("content", hasSize(lessThanOrEqualTo(Math.min(maxGardens, 10))))));
    }

    // AC3, AC4, AC6
    @Given("I enter a search string {string} into the search box")
    public void iEnterASearchStringIntoTheSearchBox(String query) throws Exception {
        // Perform the GET request to the "/browse-gardens" endpoint with a query
        mockMvc.perform(get("/browse-gardens")
                        .param("q", query)
                        .with(csrf()))
                .andExpect(status().isOk())
                // Check results returned
                .andExpect(model().attribute("q", query));
    }

    // AC3, AC5
    @When("I click the search button labelled {string} or the magnifying glass icon")
    public void iClickTheSearchButtonLabelledOrTheMagnifyingGlassIcon(String buttonLabel) throws Exception{
        // Clicking the button by submitting the search query with CSRF protection
        mockMvc.perform(get("/browse-gardens")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    // AC3
    @Then("I am shown only gardens whose names or plants include {string}")
    public void iAmShownOnlyGardensWhoseNamesOrPlantsInclude(String query) throws Exception{
        // Ensure the search results only include gardens that match the search query in either name or plants
        mockMvc.perform(get("/browse-gardens")
                        .param("q", query)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("gardenPage"))
                .andExpect(model().attribute("gardenPage", hasProperty("content", everyItem(
                        allOf(
                                // Check that test garden with test plant is returned
                                hasProperty("name", containsString("Public Test Garden"))
                        )
                ))));
    }

    // AC4
    @When("I press the Enter key")
    public void iPressTheEnterKey() throws Exception {
        // User presses enter button
    }

    // AC4
    @Then("the results are shown as if I clicked the search button")
    public void theResultsAreShownAsIfIClickedTheSearchButton() throws Exception {
        // Ensure the same behavior as clicking the search button, i.e., search results are displayed
        mockMvc.perform(get("/browse-gardens")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("gardenPage"))
                .andExpect(model().attribute("gardenPage", hasProperty("content", everyItem(
                        allOf(
                                // Check that test garden with test plant is returned
                                hasProperty("name", containsString("Public Test Garden"))
                        )
                ))));
    }

    // AC5
    @Given("I enter a search string {string} that has no matches")
    public void iEnterASearchStringThatHasNoMatches(String query) throws Exception {
        // Perform the GET request to the "/browse-gardens" endpoint with a query that has no matches
        mockMvc.perform(get("/browse-gardens")
                        .param("q", query)
                        .with(csrf()))
                .andExpect(status().isOk())
                // Ensure the query is processed
                .andExpect(model().attribute("q", query))
                // Ensure no results are returned
                .andExpect(model().attribute("gardenPage", hasProperty("content", hasSize(0))));
    }

    // AC5
    @Then("a message tells me {string}")
    public void aMessageTellsMe(String expectedMessage) throws Exception {
        // Same query as step above
        String query = "unknown";

        // Perform the query call that would result in no matches and trigger the message
        mockMvc.perform(get("/browse-gardens")
                        .param("q", query)
                        .with(csrf()))
                .andExpect(status().isOk())
                // Ensure the "noResults" message is present in the model and matches the expected message
                .andExpect(model().attribute("noResults", is(expectedMessage)));
    }

    // AC6
    @When("there are more than {int} gardens")
    public void thereAreMoreThanGardens(Integer gardenCount) throws Exception {
        String query = "TestPlant";

        // Check the first page for 10 results
        mockMvc.perform(get("/browse-gardens")
                        .param("q",query)
                        .with(csrf()))
                .andExpect(status().isOk())
                // Check the number of gardens in the content of gardenPage
                .andExpect(model().attribute("gardenPage", hasProperty("content", hasSize(equalTo(gardenCount)))));

        // Check the second page for less than or equal 10 results for the same search
        mockMvc.perform(get("/browse-gardens")
                        .param("q",query)
                        .param("page", "2")
                        .with(csrf()))
                .andExpect(status().isOk())
                // Check the number of gardens in the content of gardenPage
                .andExpect(model().attribute("gardenPage", hasProperty("content", hasSize(lessThanOrEqualTo(gardenCount)))));
    }

    // AC6
    @Then("the results are paginated with {int} per page")
    public void theResultsArePaginatedWithPerPage(Integer resultsPerPage) throws Exception {
        String query = "TestPlant";

        // Check the first page for 10 results
        mockMvc.perform(get("/browse-gardens")
                        .param("q",query)
                        .with(csrf()))
                .andExpect(status().isOk())
                // Check the number of gardens in the content of gardenPage
                .andExpect(model().attribute("gardenPage", hasProperty("content", hasSize(equalTo(resultsPerPage)))));

        // Check the second page for 10 results
        mockMvc.perform(get("/browse-gardens")
                        .param("q",query)
                        .param("page", "2")
                        .with(csrf()))
                .andExpect(status().isOk())
                // Check the number of gardens in the content of gardenPage
                .andExpect(model().attribute("gardenPage", hasProperty("content", hasSize(equalTo(resultsPerPage)))));
    }

    // AC7, AC8
    @Given("I am on any page of results")
    public void iAmOnAnyPageOfResults() {
        // Assume user is on the browse gardens page
    }

    // AC7
    @When("I click \"first\" underneath the results")
    public void iClickFirstUnderneathTheResults() throws Exception {
        mockMvc.perform(get("/browse-gardens")
                        // Simulate clicking the first page button
                        .param("page", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("gardenPage"));
    }

    // AC7
    @Then("I am taken to the first page")
    public void iAmTakenToTheFirstPage() throws Exception {
        mockMvc.perform(get("/browse-gardens")
                        // First page
                        .param("page", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                // Checks if page number is 0 (first page in this test context)
                .andExpect(model().attribute("gardenPage", hasProperty("number", is(0))));
    }

    // AC8
    @When("I click \"last\" underneath the results")
    public void iClickLastUnderneathTheResults() throws Exception {
        mockMvc.perform(get("/browse-gardens")
                        // Simulate clicking the last page button
                        .param("page", "2")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("gardenPage"));
    }

    // AC8
    @Then("I am taken to the last page")
    public void iAmTakenToTheLastPage() throws Exception {
        mockMvc.perform(get("/browse-gardens")
                        .param("page", "2")
                        .with(csrf()))
                .andExpect(status().isOk())
                // Checks if page number is 1 (last page in this test context)
                .andExpect(model().attribute("gardenPage", hasProperty("number", is(1))));
    }

    // AC9
    @Given("I click any page navigation button")
    public void iClickAnyPageNavigationButton() {
        // Assume user clicks any pagination page button
    }

    // AC9
    @Then("I am never taken before the first page or beyond the last page")
    public void iAmNeverTakenBeforeTheFirstOrBeyondTheLastPage() throws Exception {
        // Test navigating before the first page
        mockMvc.perform(get("/browse-gardens")
                        // Trying to access page 0 (before first)
                        .param("page", "0")
                        .with(csrf()))
                // Redirect to the first page (expected behavior)
                .andExpect(status().is3xxRedirection());

        // Test navigating beyond the last page
        mockMvc.perform(get("/browse-gardens")
                        // Beyond the last page (in this test context)
                        .param("page", "3")
                        .with(csrf()))
                // Redirect to the last page
                .andExpect(status().is3xxRedirection());
    }

    // AC10
    @Given("I am on page {int} with {int} results")
    public void iAmOnPageWithResults(int pageNumber, int resultsPerPage) throws Exception {
        mockMvc.perform(get("/browse-gardens")
                        .param("page", String.valueOf(pageNumber))
                        .param("results", String.valueOf(resultsPerPage))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("gardenPage"))
                // Check the correct page
                .andExpect(model().attribute("gardenPage", hasProperty("number", is(pageNumber - 1))))
                // Check the number of public gardens is correct
                .andExpect(model().attribute("gardenPage", hasProperty("size", is(resultsPerPage))));
    }

    // AC10
    @Then("I should see links for pages {int}, {int}")
    public void iShouldSeeLinksForPages(int firstPage, int nextPage) throws Exception {
        mockMvc.perform(get("/browse-gardens")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("gardenPage"))
                // Check that the total pages are greater than or equal to the 2 pages it should have
                .andExpect(model().attribute("gardenPage", hasProperty("totalPages", equalTo(nextPage))));
    }

    // AC11
    @Given("I click on page number {int}")
    public void iClickOnPageNumber(int pageNumber) throws Exception {
        mockMvc.perform(get("/browse-gardens")
                        .param("page", String.valueOf(pageNumber))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("gardenPage"))
                // Check if the page number is the one that was clicked (-1 because its zero-based index)
                .andExpect(model().attribute("gardenPage", hasProperty("number", is(pageNumber - 1))));
    }

    // AC11
    @Then("I am navigated to that page")
    public void iAmNavigatedToThatPage() throws Exception {
        mockMvc.perform(get("/browse-gardens")
                        .param("page", "2")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("gardenPage"))
                // Page number is 2 (but is index 1)
                .andExpect(model().attribute("gardenPage", hasProperty("number", is(1))));
    }

    // AC12
    @Given("I am on any page")
    public void iAmOnAnyPage() throws Exception {
        mockMvc.perform(get("/browse-gardens")
                        // Assuming we're on the second page for this test
                        .param("page", "2")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("gardenPage"));
    }

    // AC12
    @Then("I see the text {string}")
    public void iSeeTheText(String expectedText) throws Exception {
        // Fetch the page to get the current results
        mockMvc.perform(get("/browse-gardens")
                        .param("page", "2")
                        .with(csrf()))
                .andExpect(status().isOk())
                // Verify the text showing results is correctly rendered
                .andExpect(MockMvcResultMatchers.content().string(containsString(expectedText)));
    }
}
