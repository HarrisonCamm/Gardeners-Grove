package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import nz.ac.canterbury.seng302.gardenersgrove.controller.CreatePlantController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.EditPlantController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.ViewGardenController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;
import io.cucumber.java.en.*;

import org.mockito.Mockito;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class
RecordGardenPlantsSteps {
    private static MockMvc mockMvcCreatePlant;
    private static MockMvc mockMvcViewGarden;
    private MockMvc mockMvcTemp;
    private MvcResult mvcResult;
    private MockHttpSession mockSession;
    @MockBean
    private static PlantService plantService;

    @MockBean
    private static GardenService gardenService;

    @MockBean
    private static UserService userService;

    @MockBean
    private static ImageService imageService;

    @MockBean
    private static TagService tagService;

    @MockBean
    private static WeatherService weatherService;

    @MockBean
    private static ModerationService moderationService;

    @MockBean
    private static AlertService alertService;

    private static Map<Long, Plant> mockPlantDB;
    private static Plant testPlant;
    private static Garden testGarden;
    private String plantName = "";
    private String plantCount = "";
    private String plantDescription = "";
    private String plantDatePlanted = "";


    private static Plant copyPlant(Plant plant) {
        Plant out = new Plant(plant.getGarden(),plant.getName(), plant.getCount(), plant.getDescription(), plant.getDatePlanted());
        out.setId(plant.getId());
        return out;
    }

    @Before
    public void setUp() {
        mockSession = new MockHttpSession();
    }
    @Before
    public static void before_or_after_all() {
        plantService = Mockito.mock(PlantService.class);
        gardenService = Mockito.mock(GardenService.class);
        userService = Mockito.mock(UserService.class);
        imageService = Mockito.mock(ImageService.class);
        tagService = Mockito.mock(TagService.class);
        weatherService = Mockito.mock(WeatherService.class);
        moderationService = Mockito.mock(ModerationService.class);
        alertService = Mockito.mock(AlertService.class);


        if (mockPlantDB != null) {mockPlantDB.clear();} //Clear pseudo plant database in between examples


        //Logged in user handling code taken from RequestPasswordSteps.java credit OCL28
        User loggedInUser = new User("user@gmail.com", "Test", "User", "p@ssw0rd123");
        when(userService.getAuthenticatedUser()).thenReturn(loggedInUser);
        when(userService.getUserByEmail(any(String.class))).thenReturn(loggedInUser);
        when(userService.emailExists(any(String.class))).thenReturn(true);
        when(userService.updateUserPassword(any(User.class), any(String.class))).thenReturn(loggedInUser);

        mockPlantDB = new HashMap<>();
        testGarden = new Garden("testGardenName", new Location(), "1");
        testGarden.setId(1L);
        testGarden.setOwner(loggedInUser);

        //Mock gardenService methods called by test pages
        when(gardenService.findGarden(any(Long.class))).thenReturn(Optional.of(testGarden));

        //Mock PlantService to access mockPlantDB list instead of a real database
        when(plantService.addPlant(any(Plant.class))).thenAnswer(invocation -> {
            Plant plant = invocation.getArgument(0);
            if (plant.getId() == null) {plant.setId((long) mockPlantDB.size());}
            mockPlantDB.put( plant.getId(), copyPlant(plant) );
            return plant;
        });
        when(plantService.findPlant(any(Long.class))).thenAnswer(invocation -> Optional.of( copyPlant(mockPlantDB.get((Long) invocation.getArgument(0)))));     //Suspicious stew
        when(plantService.getGardenPlant(any(Long.class))).thenAnswer(invocation ->
                mockPlantDB.values().stream()
                        .map(RecordGardenPlantsSteps::copyPlant)
                        .collect(Collectors.toCollection(ArrayList::new))
        );

        //Mock GardenService to only return testgarden
        //Generated by copilot 🤪
        when(gardenService.getGardens()).thenReturn(Collections.singletonList(testGarden));
        when(gardenService.getOwnedGardens(any(Long.class))).thenReturn(Collections.singletonList(testGarden));
        when(gardenService.findGarden(any(Long.class))).thenReturn(Optional.of(testGarden));

        //Create Controller objects for MockMVC pages
        CreatePlantController CreatePlantController = new CreatePlantController(plantService, gardenService, userService, imageService);
        ViewGardenController ViewGardenController = new ViewGardenController(gardenService, plantService, userService, imageService, tagService, weatherService, moderationService, alertService);

        //Build MockMVC page
        mockMvcCreatePlant = MockMvcBuilders.standaloneSetup(CreatePlantController).build();
        mockMvcViewGarden = MockMvcBuilders.standaloneSetup(ViewGardenController).build();
    }

    //AC 1
    @Given("I am on the garden details page for a garden I own")
    public void i_am_on_the_garden_details_page_for_a_garden_i_own() throws Exception {
        this.mvcResult = mockMvcViewGarden.perform(get("/view-garden?gardenID=" + testGarden.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("viewGardenDetailsTemplate"))
                .andReturn();
    }

    //AC 2, 3, 4, 5, 6, 7
    @Given("I am on the add plant form")
    public void i_am_on_the_add_plant_form() throws Exception {
        this.mvcResult = mockMvcCreatePlant.perform(get("/create-plant?gardenID=" + testGarden.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("createPlantFormTemplate"))
                .andReturn();
    }

    //AC 2
    @Given("I enter valid values for the {string} and optionally a {string}, {string}, and a {string}")
    public void i_enter_valid_values(String plantName, String plantCount, String plantDescription, String plantDatePlanted) {
        this.plantName = plantName;
        this.plantCount = plantCount;
        this.plantDescription = plantDescription;
        this.plantDatePlanted = plantDatePlanted;
        this.mockMvcTemp = mockMvcViewGarden;
    }

    //AC 3
    @Given("I enter an empty or invalid plant {string}")
    public void i_enter_an_empty_or_invalid_plant_name(String plantName) {
        this.plantName = plantName;
        this.mockMvcTemp = mockMvcCreatePlant;
    }

    //AC 4
    @Given("I enter a description with {int}")
    public void i_enter_a_description_that_is_longer_than_512_characters(int descriptionLength) {
        this.plantName = "plant";
        this.plantDescription = "a".repeat(descriptionLength);
        this.mockMvcTemp = mockMvcCreatePlant;
    }

    //AC 5
    @Given("I enter an invalid {string}")
    public void i_enter_an_invalid_count(String plantCount) {
        this.plantName = "plant";
        this.plantCount = plantCount;
        this.mockMvcTemp = mockMvcCreatePlant;
    }

    //AC 6
    @Given("I enter a {string} that is not in the Aotearoa NZ format")
    public void i_enter_a_date_that_is_not_in_the_aotearoa_nz_format(String plantDatePlanted) {
        this.plantName = "plant";
        this.plantDatePlanted = plantDatePlanted;
        this.mockMvcTemp = mockMvcCreatePlant;
    }

    //AC 1
    @When("I click the add new plant button")
    public void i_click_on_the_add_new_plant_button() throws Exception {
        this.mvcResult = mockMvcCreatePlant.perform(get("/create-plant?gardenID=" + testGarden.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("createPlantFormTemplate"))
                .andReturn();
    }

    //AC 2, 3, 4, 5, 6
    @When("I click the submit button on the add plant form")
    public void i_click_the_submit_add_plant_button() throws Exception {
        testPlant = new Plant(testGarden, plantName, plantCount, plantDescription, plantDatePlanted);

        this.mvcResult = mockMvcCreatePlant.perform(post("/create-plant")
                        .param("gardenID", String.valueOf(testGarden.getId()))
                        .param("name", plantName)
                        .param("count", plantCount)
                        .param("description", plantDescription)
                        .param("datePlanted", plantDatePlanted)
                        .session(mockSession)
                        .flashAttr("plant", testPlant))
                .andReturn();


        mockSession = (MockHttpSession) mvcResult.getRequest().getSession();
        this.mvcResult = mockMvcTemp.perform(get(this.mvcResult.getResponse().getRedirectedUrl()))
                .andReturn();
    }

    //AC 7
    @When("I click the cancel button on the add plant form")
    public void i_click_the_cancel_add_plant_button() throws Exception {
        this.mvcResult = mockMvcViewGarden.perform(get("/view-garden?gardenID=" + testGarden.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("viewGardenDetailsTemplate"))
                .andReturn();
    }

    //AC 1
    @Then("I see an add plant form")
    public void i_see_an_add_plant_form() {
        assertEquals("createPlantFormTemplate", this.mvcResult.getModelAndView().getViewName());
        assertEquals(testGarden.getId(), this.mvcResult.getModelAndView().getModel().get("gardenID"));
    }

    //AC 2
    @Then("A new plant record is added to the garden")
    public void a_new_plant_record_is_added_to_the_garden() {
        List<Plant> gardenPlants = plantService.getGardenPlant(testGarden.getId());
        Plant storedPlant = gardenPlants.get(0);
        assertEquals(storedPlant.getId(), testPlant.getId());
        assertEquals(storedPlant.getName(), testPlant.getName());
        assertEquals(storedPlant.getCount(), testPlant.getCount());
        assertEquals(storedPlant.getDescription(), testPlant.getDescription());
        assertEquals(storedPlant.getDatePlanted(), testPlant.getDatePlanted());

    }

    //AC 2, 7
    @Then("I am taken back to the garden details page from add plant page")
    public void i_am_taken_back_to_the_garden_details_page_from_add_plant_page() {
        assertEquals("viewGardenDetailsTemplate", this.mvcResult.getModelAndView().getViewName());
        assertEquals(testGarden.getId(), this.mvcResult.getModelAndView().getModel().get("gardenID"));
    }

    //AC 3, 4, 5, 6
    @Then("An error message tells me {string} on the add plant form")
    public void an_error_message_on_the_add_plant_form(String errorMessage) {
        assertEquals("createPlantFormTemplate", this.mvcResult.getModelAndView().getViewName());
        assertEquals(testGarden.getId(), this.mvcResult.getModelAndView().getModel().get("gardenID"));
        List <String> errors = new ArrayList<>();
        errors.add((String) mockSession.getAttribute("nameError"));
        errors.add((String) mockSession.getAttribute("countError"));
        errors.add((String) mockSession.getAttribute("descriptionError"));
        errors.add((String) mockSession.getAttribute("dateError"));
        assertTrue(errors.contains(errorMessage));
    }
    //AC 7
    @Then("No changes are made to the garden")
    public void no_changes_are_made_to_the_garden() {
        List<Plant> gardenPlants = plantService.getGardenPlant(testGarden.getId());
        assertTrue(gardenPlants.isEmpty()); //test garden started with no plants so this checks it still has no plants
    }
}