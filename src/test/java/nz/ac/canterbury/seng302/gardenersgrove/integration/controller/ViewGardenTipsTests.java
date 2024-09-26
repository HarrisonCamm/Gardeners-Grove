package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import jakarta.persistence.EntityNotFoundException;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.TransactionRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;



@SpringBootTest
@ActiveProfiles("cucumber")
@AutoConfigureMockMvc
public class ViewGardenTipsTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GardenRepository gardenRepository;

    @Autowired GardenService gardenService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    private User sarah;
    private User inaya;
    private User liam;
    private Garden sarahsGarden;
    private Garden inayasGarden;
    private Garden liamsGarden;

    @BeforeEach
    public void setUp() {
        sarah = userRepository.save(new User("sarah","sarah",null,null,null));
        inaya = userRepository.save(new User("inaya","inaya",null,null,null));
        liam = userRepository.save(new User("liam","liam",null,null,null));

        sarahsGarden = new Garden("sarahsGarden", null, null, sarah);
        sarahsGarden.setIsPublic(true);

        inayasGarden = new Garden("inayasGarden", null, null, sarah);
        inayasGarden.setIsPublic(true);

        liamsGarden = new Garden("liamsGarden", null, null, sarah);
        liamsGarden.setIsPublic(true);

        sarahsGarden = gardenRepository.save(sarahsGarden);
        inayasGarden = gardenRepository.save(inayasGarden);
        liamsGarden = gardenRepository.save(liamsGarden);

        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            int blooms = invocation.getArgument(1);
            user.setBloomBalance(user.getBloomBalance() - blooms);
            userRepository.save(user);
            return null;
        }).when(userService).chargeBlooms(any(User.class), anyInt());

        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            int blooms = invocation.getArgument(1);
            user.setBloomBalance(user.getBloomBalance() + blooms);
            userRepository.save(user);
            return null;
        }).when(userService).addBlooms(any(User.class), anyInt());
    }

    @Test
    @WithMockUser
    public void claimTips_withOneTipTransaction_CorrectlyChargesAndPays() throws Exception{
        Integer totalTips = 20;
        String totalTipsString = totalTips.toString();
        Integer initialBalanceSarah = sarah.getBloomBalance();
        Integer initialBalanceLiam = liam.getBloomBalance();

        Mockito.when(userService.getAuthenticatedUser()).thenReturn(liam);      // 'Login' as liam


        mockMvc.perform(post("/tip-blooms")    // Tip sarahsGarden
                .param("gardenID", sarahsGarden.getId().toString())
                .param("tipAmount", totalTipsString)
                .with(csrf()));

        Assertions.assertEquals(1, transactionService.retrieveGardenTips(sarahsGarden).size(), "There should be 1 transaction from the tip from liam");

        liam = userRepository.findById(liam.getUserId()).orElseThrow(() -> new EntityNotFoundException("Receiver not found"));    //Update liam from database
        sarah = userRepository.findById(sarah.getUserId()).orElseThrow(() -> new EntityNotFoundException("Receiver not found"));    //Update sarah from database
        Assertions.assertEquals(initialBalanceLiam - totalTips, liam.getBloomBalance(), "Tips should now be subtracted to Liam's balance");
        Assertions.assertEquals(initialBalanceSarah, sarah.getBloomBalance(),  "Tips should not be added to Sarah's balance until she claims the tips");


        Mockito.when(userService.getAuthenticatedUser()).thenReturn(sarah);      // 'Login' as sarah

        mockMvc.perform(post("/claim-tips")
                .param("gardenID", sarahsGarden.getId().toString())
                .with(csrf()));

        Assertions.assertEquals(0, transactionService.retrieveGardenTips(sarahsGarden).size(), "All tip transactions should be consumed by claim-tips post");

        sarah = userRepository.findById(sarah.getUserId()).orElseThrow(() -> new EntityNotFoundException("Receiver not found"));    //Update sarah from database
        liam = userRepository.findById(liam.getUserId()).orElseThrow(() -> new EntityNotFoundException("Receiver not found"));    //Update liam from database

        Assertions.assertEquals(initialBalanceSarah + totalTips, sarah.getBloomBalance(), "Tips should now be added to Sarah's balance");
        Assertions.assertEquals(initialBalanceLiam - totalTips, liam.getBloomBalance(), "Tips should be subtracted to Liam's balance only once");
    }
}
