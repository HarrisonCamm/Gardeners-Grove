package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;
import io.cucumber.java.en.*;

import org.mockito.Mockito;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class EditGardenPlantsSteps {
    private static MockMvc mockMvcEditPlant;
    private static MockMvc mockMvcViewGarden;
    private MvcResult mvcResult;

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
    private static TransactionService transactionService;

    @MockBean
    private static AlertService alertService;

    private static Map<Long, Plant> mockPlantDB;

    private static Garden testGarden;

    private static Plant copyPlant(Plant plant) {
        Plant out = new Plant(plant.getGarden(),plant.getName(), plant.getCount(), plant.getDescription(), plant.getDatePlanted());
        out.setId(plant.getId());
        return out;
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
        transactionService = Mockito.mock(TransactionService.class);


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
        when(plantService.getGardenPlant(any(Long.class))).thenAnswer(invocation -> new ArrayList<>(mockPlantDB.values()));
        when(plantService.getGardenPlant(any(Long.class))).thenAnswer(invocation ->
                mockPlantDB.values().stream()
                        .map(EditGardenPlantsSteps::copyPlant)
                        .collect(Collectors.toCollection(ArrayList::new))
        );

        //Mock GardenService to only return testgarden
        //Generated by copilot 🤪
        when(gardenService.getGardens()).thenReturn(Collections.singletonList(testGarden));
        when(gardenService.getOwnedGardens(any(Long.class))).thenReturn(Collections.singletonList(testGarden));
        when(gardenService.findGarden(any(Long.class))).thenReturn(Optional.of(testGarden));

        //Create Controller objects for MockMVC pages
        EditPlantController EditPlantController = new EditPlantController(plantService, gardenService, userService, imageService);
        ViewGardenController ViewGardenController = new ViewGardenController(gardenService, plantService, userService, imageService, tagService, weatherService, moderationService, alertService, transactionService);

        //Build MockMVC page
        mockMvcEditPlant = MockMvcBuilders.standaloneSetup(EditPlantController).build();
        mockMvcViewGarden = MockMvcBuilders.standaloneSetup(ViewGardenController).build();
    }
    //AC1-9
    @Given("I have {int} plants in my garden with the details {string}, {string}, {string}, and {string}")
    public void i_have_plants_in_my_garden_with_the_details_and(int numPlants, String plantName, String plantCount, String plantDesc, String plantDatePlanted) {
        for (int i = 0; i<numPlants; i++){
            plantService.addPlant(new Plant(testGarden, plantName, plantCount, plantDesc, plantDatePlanted));
        }
        assertNotNull(mockPlantDB);      //Probably a waste of an assertion
        assertEquals(numPlants, mockPlantDB.size()); //Another sanity check assertion
    }
    //AC1-9
    @And("I am on the garden details page")
    public void i_am_on_the_garden_details_page() throws Exception{
        this.mvcResult = mockMvcViewGarden.perform(get("/view-garden?gardenID=" + testGarden.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("viewGardenDetailsTemplate"))
                .andReturn();
    }
    //AC1
    @Then("I should see a list of {int} plants")
    public void i_should_see_a_list_of_plants(int numPlantsListed) {
        Assertions.assertNotNull(mvcResult.getModelAndView().getModel().get("plants"));
        Assertions.assertEquals(numPlantsListed, ((List<?>) mvcResult.getModelAndView().getModel().get("plants")).size());
    }
    //AC1
    @And("they have the following details displayed {string}, {string}, {string}, and {string}")
    public void they_have_the_following_details_displayed_and(String plantName, String plantCount, String plantDesc, String plantDatePlanted) {
        ModelAndView modelAndView = mvcResult.getModelAndView();
        List<Plant> plants = (List<Plant>) modelAndView.getModel().get("plants");
        for (Plant plant : plants) {
            Assertions.assertEquals(plantName, plant.getName());
            Assertions.assertEquals(plantCount, plant.getCount());
            Assertions.assertEquals(plantDesc, plant.getDescription());
            Assertions.assertEquals(plantDatePlanted, plant.getDatePlanted());
        }
    }
    //AC3-9
    @When("I hit the edit button for the first plant")
    public void i_hit_the_edit_button_for_the_first_plant() throws Exception{
        this.mvcResult = mockMvcEditPlant.perform(get("/edit-plant?plantID=" + 0))
                .andExpect(status().isOk())
                .andExpect(view().name("editPlantFormTemplate"))
                .andReturn();
    }
    //AC3
    @Then("The edit plant form is prefilled with the details {string}, {string}, {string}, and {string}")
    public void the_edit_plant_form_is_prefilled_with_the_details_and(String plantName, String plantCount, String plantDesc, String plantDatePlanted) {
        //Assert all plant details are correct
        Assertions.assertEquals(plantName, ((Plant) mvcResult.getModelAndView().getModel().get("plant")).getName());
        Assertions.assertEquals(plantCount, ((Plant) mvcResult.getModelAndView().getModel().get("plant")).getCount());
        Assertions.assertEquals(plantDesc, ((Plant) mvcResult.getModelAndView().getModel().get("plant")).getDescription());
        Assertions.assertEquals(plantDatePlanted, ((Plant) mvcResult.getModelAndView().getModel().get("plant")).getDatePlanted());
        //Assert separate datePlanted attribute used for post mapping is correct
        Assertions.assertEquals(plantDatePlanted, (mvcResult.getModelAndView().getModel().get("datePlanted")));
    }
    //AC4-8
    @And("I hit the save button with the details {string}, {string}, {string}, and {string}")
    public void i_hit_the_save_button_with_the_details_and(String plantNameNew, String plantCountNew, String plantDescNew, String plantDatePlantedNew) throws Exception  {
        Plant updatedPlant = new Plant(testGarden, plantNameNew, plantCountNew, plantDescNew, plantDatePlantedNew);
        this.mvcResult = mockMvcEditPlant.perform(put("/edit-plant")
                        .param("plantID", "0") // Assuming the first plant in the mock DB
                        .param("datePlanted", plantDatePlantedNew)  //Technically passing in the new date but idk how to fix
                        .flashAttr("plant", updatedPlant))
                .andReturn();
    }
    //AC6
    @When("I hit the save button with a description {int} characters long and the details {string}, {string}, and {string}")
    public void i_hit_the_save_button_with_a_description_characters_long_and_the_details_and(int descLength, String plantNameNew, String plantCountNew, String plantDatePlantedNew) throws Exception {
        Plant updatedPlant = new Plant(testGarden, plantNameNew, plantCountNew, "a".repeat(descLength), plantDatePlantedNew);
        this.mvcResult = mockMvcEditPlant.perform(put("/edit-plant")
                        .param("plantID", "0") // Assuming the first plant in the mock DB
                        .param("datePlanted", plantDatePlantedNew)  //Technically passing in the new date but idk how to fix
                        .flashAttr("plant", updatedPlant))
                .andReturn();
    }
    //AC4
    @Then("The plant details are updated with {string}, {string}, {string}, and {string}")
    public void the_plant_details_are_updated_with_and(String plantNameNew, String plantCountNew, String plantDescNew, String plantDatePlantedNew) {
        Plant plant = mockPlantDB.get(0L); // All tests refer to first plant in mock database
        assertEquals(plantNameNew, plant.getName());
        assertEquals(plantCountNew, plant.getCount());
        assertEquals(plantDescNew, plant.getDescription());
        assertEquals(plantDatePlantedNew, plant.getDatePlanted());
    }
    //AC5-9
    @Then("The plant details are not updated and stay as {string}, {string}, {string}, and {string}")
    public void the_plant_details_are_not_updated_and_stay_as_and(String plantName, String plantCount, String plantDesc, String plantDatePlanted) {
        Plant plant = mockPlantDB.get(0L); // All tests refer to first plant in mock database
        assertEquals(plantName, plant.getName());
        assertEquals(plantCount, plant.getCount());
        assertEquals(plantDesc, plant.getDescription());
        assertEquals(plantDatePlanted, plant.getDatePlanted());
    }
    //AC5-8
    @And("An error message tells me {string}")
    public void an_error_message_tells_me(String errorMessage) {
        Map<String, Object> test = this.mvcResult.getModelAndView().getModel();     //get all model attributes as a dictionary
        List<Object> test2 = new ArrayList<>(test.values());        //Get
        Assertions.assertTrue(test2.contains(errorMessage));
    }
    //AC9
    @And("I hit the cancel button on the edit plant form")
    public void i_hit_the_cancel_button_on_the_edit_plant_form() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        String cancelButtonEndpoint = (String) this.mvcResult.getModelAndView().getModel().get("lastEndpoint");
        this.mvcResult = mockMvcViewGarden.perform(get(cancelButtonEndpoint))       //attempting to use last endpoint as cancel button would use
                .andExpect(status().isOk())                                         //Will fail if not a view garden link as last endpoint as it is mockmvc only for view garden page
                .andExpect(view().name("viewGardenDetailsTemplate"))
                .andReturn();
    }
    //AC9
    @And("I am taken back to the garden details page")
    public void i_am_taken_back_to_the_garden_details_page() {
        Assertions.assertEquals("viewGardenDetailsTemplate", this.mvcResult.getModelAndView().getViewName());
        Assertions.assertEquals(testGarden.getId(), this.mvcResult.getModelAndView().getModel().get("gardenID"));
    }
}
