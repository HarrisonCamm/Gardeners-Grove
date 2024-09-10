package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
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

    // General setup for MockMvc and user
    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    // Parameterized setup for gardens
    @Before("@SingleGarden or @MultipleGardens")
    public void setupGardens(io.cucumber.java.Scenario scenario) {
        int numberOfGardens = scenario.getSourceTagNames().contains("@MultipleGardens") ? 19 : 1;
        createGardens(numberOfGardens);
    }

    private void createGardens(int count) {
        // Check if the gardenOwner already exists, if not create it
        if (gardenOwner == null) {
            gardenOwner = new User("inaya@email.com", "Inaya", true, "", "Password1!", "1990-01-01");
            userService.addUser(gardenOwner);
        }

        // Create gardens
        IntStream.rangeClosed(1, count).forEach(i -> {
            // Create a new unique location for each garden
            Location location = new Location("Test Street " + i, "Test Suburb", "Test City", "1234", "Country");

            String gardenName = "Public Test Garden" + (count == 1 ? "" : i);
            Garden garden = new Garden(gardenName, location, "100", gardenOwner, "A public garden " + (count == 1 ? "" : i));

            // Mark the garden as public
            garden.setIsPublic(true);
            gardenService.addGarden(garden);

            // Create and add plants to the garden
            Plant testPlant = new Plant(garden, "TestPlant" + (count == 1 ? "" : i), "5", "Testy plant" + (count == 1 ? "" : i), "2024-09-05");
            plantService.addPlant(testPlant);

            // Set the publicGarden to the current garden if this is a single garden setup
            if (i == count) {
                publicGarden = garden;
            }
        });
    }

    // AC1
    @Given("a garden has been marked as public")
    public void gardenMarkedAsPublic() {
        // This is done in setup
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
                                // Plant Details of the garden
                                hasProperty("name", is("TestPlant")),
                                // Quantity of plants
                                hasProperty("count", is("5")),
                                hasProperty("description", is("Testy plant")),
                                hasProperty("datePlanted", is("2024-09-05"))
                        )
                )));
    }

    // AC2
    @Given("I am anywhere on the system")
    public void iAmAnywhereOnTheSystem() throws Exception {
        // Main for test purposes
        mockMvc.perform(get("/main"))
                .andExpect(status().isOk())
                // Attributes on the main page
                .andExpect(model().attributeExists("name"));
    }

    @When("I click the {string} button on the navigation bar")
    public void iClickTheButtonOnTheNavigationBar(String browserGardens) throws Exception {
        // change button name to actual url
        browserGardens = "/browse-gardens";

        // Click the browse gardens button
        mockMvc.perform(get(browserGardens))
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
                        .param("q", query))
                .andExpect(status().isOk())
                // Check results returned
                .andExpect(model().attribute("q", query));
    }

    // AC3, AC5
    @When("I click the search button labelled {string} or the magnifying glass icon")
    public void iClickTheSearchButtonLabelledOrTheMagnifyingGlassIcon(String buttonLabel) throws Exception{
        // Clicking the button by submitting the search query
        mockMvc.perform(get("/browse-gardens"))
                .andExpect(status().isOk());
    }

    // AC3 AC4
    @Then("I am shown only gardens whose names or plants include {string}")
    public void iAmShownOnlyGardensWhoseNamesOrPlantsInclude(String query) throws Exception{
        // Ensure the search results only include gardens that match the search query in either name or plants
        // The results returned in this case, "Public Test Garden"
        mockMvc.perform(get("/browse-gardens")
                        .param("q", query))
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
        // It performs the post-request search below in the next step
    }

    // AC5
    @Given("I enter a search string {string} that has no matches")
    public void iEnterASearchStringThatHasNoMatches(String query) throws Exception {
        // Perform the GET request to the "/browse-gardens" endpoint with a query that has no matches
        mockMvc.perform(get("/browse-gardens")
                        .param("q", query))
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
                        .param("q", query))
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
                        .param("q",query))
                .andExpect(status().isOk())
                // Check the number of gardens in the content of gardenPage
                .andExpect(model().attribute("gardenPage", hasProperty("content", hasSize(equalTo(gardenCount)))));

        // Check the second page for less than or equal 10 results for the same search
        mockMvc.perform(get("/browse-gardens")
                        .param("q",query)
                        .param("page", "2"))
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
                        .param("q",query))
                .andExpect(status().isOk())
                // Check the number of gardens in the content of gardenPage
                .andExpect(model().attribute("gardenPage", hasProperty("content", hasSize(equalTo(resultsPerPage)))));

        // Check the second page for 10 results
        mockMvc.perform(get("/browse-gardens")
                        .param("q",query)
                        .param("page", "2"))
                .andExpect(status().isOk())
                // Check the number of gardens in the content of gardenPage
                .andExpect(model().attribute("gardenPage", hasProperty("content", hasSize(equalTo(resultsPerPage)))));
    }

    // AC7, AC8
    @Given("I am on any page of results")
    public void iAmOnAnyPageOfResults() throws Exception {
        // Navigate to the browse gardens button
        mockMvc.perform(get("/browse-gardens"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("gardenPage"));
    }

    // AC7
    @When("I click \"first\" underneath the results")
    public void iClickFirstUnderneathTheResults() throws Exception {
        mockMvc.perform(get("/browse-gardens")
                        // Simulate clicking the first page button
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("gardenPage"));
    }

    // AC7
    @Then("I am taken to the first page")
    public void iAmTakenToTheFirstPage() throws Exception {
        mockMvc.perform(get("/browse-gardens")
                        // First page
                        .param("page", "1"))
                .andExpect(status().isOk())
                // Checks if page number is 0 (first page in this test context)
                .andExpect(model().attribute("gardenPage", hasProperty("number", is(0))));
    }

    // AC8
    @When("I click \"last\" underneath the results")
    public void iClickLastUnderneathTheResults() throws Exception {
        mockMvc.perform(get("/browse-gardens")
                        // Simulate clicking the last page button
                        .param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("gardenPage"));
    }

    // AC8
    @Then("I am taken to the last page")
    public void iAmTakenToTheLastPage() throws Exception {
        mockMvc.perform(get("/browse-gardens")
                        .param("page", "2"))
                .andExpect(status().isOk())
                // Checks if page number is 1 (last page in this test context)
                .andExpect(model().attribute("gardenPage", hasProperty("number", is(1))));
    }

    // AC9
    @Given("I click any page navigation button")
    public void iClickAnyPageNavigationButton() {
        // Assume user clicks any pagination page button
        // Left empty as it just performs a post request
        // As done below
    }

    // AC9
    @Then("I am never taken before the first page or beyond the last page")
    public void iAmNeverTakenBeforeTheFirstOrBeyondTheLastPage() throws Exception {
        // Test navigating before the first page
        mockMvc.perform(get("/browse-gardens")
                        // Trying to access page 0 (before first)
                        .param("page", "0"))
                // Redirect to the first page (expected behavior)
                .andExpect(status().is3xxRedirection());

        // Test navigating beyond the last page
        mockMvc.perform(get("/browse-gardens")
                        // Beyond the last page (in this test context)
                        .param("page", "3"))
                // Redirect to the last page
                .andExpect(status().is3xxRedirection());
    }

    // AC10
    @Given("I am on page {int} with {int} results")
    public void iAmOnPageWithResults(int pageNumber, int resultsPerPage) throws Exception {
        mockMvc.perform(get("/browse-gardens")
                        .param("page", String.valueOf(pageNumber))
                        .param("results", String.valueOf(resultsPerPage)))
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
        mockMvc.perform(get("/browse-gardens"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("gardenPage"))
                // Check that the total pages are greater than or equal to the 2 pages it should have
                .andExpect(model().attribute("gardenPage", hasProperty("totalPages", equalTo(nextPage))));
    }

    // AC11
    @Given("I click on page number {int}")
    public void iClickOnPageNumber(int pageNumber) throws Exception {
        mockMvc.perform(get("/browse-gardens")
                        .param("page", String.valueOf(pageNumber)))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("gardenPage"))
                // Check if the page number is the one that was clicked (-1 because its zero-based index)
                .andExpect(model().attribute("gardenPage", hasProperty("number", is(pageNumber - 1))));
    }

    // AC11
    @Then("I am navigated to that page")
    public void iAmNavigatedToThatPage() throws Exception {
        mockMvc.perform(get("/browse-gardens")
                        .param("page", "2"))
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
                        .param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("gardenPage"));
    }

    // AC12
    @Then("I see the text {string}")
    public void iSeeTheText(String expectedText) throws Exception {
        // Fetch the page to get the current results
        mockMvc.perform(get("/browse-gardens")
                        .param("page", "2"))
                .andExpect(status().isOk())
                // Verify the text showing results is correctly rendered
                .andExpect(MockMvcResultMatchers.content().string(containsString(expectedText)));
    }
}
