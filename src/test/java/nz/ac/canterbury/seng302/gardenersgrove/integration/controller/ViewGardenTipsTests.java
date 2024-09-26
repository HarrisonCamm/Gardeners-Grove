package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import jakarta.persistence.EntityNotFoundException;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
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
class ViewGardenTipsTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GardenRepository gardenRepository;

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
    private Garden sarahs2ndGarden;
    private Garden inayasGarden;
    private Garden liamsGarden;

    @BeforeEach
    public void setUp() {
        sarah = userRepository.save(new User("sarah","sarah",null,null,null));
        inaya = userRepository.save(new User("inaya","inaya",null,null,null));
        liam = userRepository.save(new User("liam","liam",null,null,null));

        sarahsGarden = new Garden("sarah'sGarden", null, null, sarah);
        sarahsGarden.setIsPublic(true);

        sarahs2ndGarden = new Garden("sarah'sGarden", null, null, sarah);
        sarahs2ndGarden.setIsPublic(true);

        inayasGarden = new Garden("inaya'sGarden", null, null, sarah);
        inayasGarden.setIsPublic(true);

        liamsGarden = new Garden("liam'sGarden", null, null, sarah);
        liamsGarden.setIsPublic(true);

        sarahsGarden = gardenRepository.save(sarahsGarden);
        sarahs2ndGarden = gardenRepository.save(sarahs2ndGarden);
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
    void claimTips_withOneTipTransaction_CorrectlyChargesAndPays() throws Exception{
        Integer totalTips = 20;
        String totalTipsString = totalTips.toString();
        Integer initialBalanceSarah = sarah.getBloomBalance();
        Integer initialBalanceLiam = liam.getBloomBalance();

        tipGarden(sarahsGarden, liam, totalTipsString);
        Assertions.assertEquals(1, transactionService.retrieveGardenTips(sarahsGarden).size(), "There should be 1 transaction from the tip from liam");

        updateUsers();
        Assertions.assertEquals(initialBalanceLiam - totalTips, liam.getBloomBalance(), "Tips should now be subtracted to Liam's balance");
        Assertions.assertEquals(initialBalanceSarah, sarah.getBloomBalance(),  "Tips should not be added to Sarah's balance until she claims the tips");

        claimTips(sarahsGarden, sarah);
        Assertions.assertEquals(0, transactionService.retrieveGardenTips(sarahsGarden).size(), "All tip transactions should be consumed by claim-tips post");

        updateUsers();
        Assertions.assertEquals(initialBalanceSarah + totalTips, sarah.getBloomBalance(), "Tips should now be added to Sarah's balance");
        Assertions.assertEquals(initialBalanceLiam - totalTips, liam.getBloomBalance(), "Tips should be subtracted to Liam's balance only once");
    }

    @Test
    @WithMockUser
    void claimTips_withTwoTipTransactions_CorrectlyChargesAndPays() throws Exception{
        Integer totalTips = 40;
        Integer individualTips = 20;
        String individualTipsString = individualTips.toString();
        Integer initialBalanceSarah = sarah.getBloomBalance();
        Integer initialBalanceLiam = liam.getBloomBalance();
        Integer initialBalanceInaya = inaya.getBloomBalance();

        tipGarden(sarahsGarden, liam, individualTipsString);
        Assertions.assertEquals(1, transactionService.retrieveGardenTips(sarahsGarden).size(), "There should be 1 transaction from the tip from liam");

        tipGarden(sarahsGarden, inaya, individualTipsString);
        Assertions.assertEquals(2, transactionService.retrieveGardenTips(sarahsGarden).size(), "There should be 2 transactions now from liam and Inaya");

        claimTips(sarahsGarden, sarah);
        Assertions.assertEquals(0, transactionService.retrieveGardenTips(sarahsGarden).size(), "All tip transactions should be consumed by claim-tips post");

        updateUsers();
        Assertions.assertEquals(initialBalanceSarah + totalTips, sarah.getBloomBalance(), "Tips should now be added to Sarah's balance");
        Assertions.assertEquals(initialBalanceLiam - individualTips, liam.getBloomBalance(), "Tips should be subtracted to Liam's balance only once");
        Assertions.assertEquals(initialBalanceInaya - individualTips, inaya.getBloomBalance(), "Tips should be subtracted to Liam's balance only once");
    }

    @Test
    @WithMockUser
    void claimTips_withTwoTippedGardens_TipIsOnlyAddedToTippedGarden() throws Exception {
        Integer individualTips = 20;
        String individualTipsString = individualTips.toString();
        Integer initialBalanceSarah = sarah.getBloomBalance();
        Integer initialBalanceLiam = liam.getBloomBalance();

        tipGarden(sarahsGarden, liam, individualTipsString);
        Assertions.assertEquals(1, transactionService.retrieveGardenTips(sarahsGarden).size(), "There should be 1 transaction from the tip from liam");

        tipGarden(liamsGarden, inaya, individualTipsString);
        Assertions.assertEquals(1, transactionService.retrieveGardenTips(sarahsGarden).size(), "The tip to another garden shouldn't be added to sarah");

        claimTips(sarahsGarden, sarah);
        Assertions.assertEquals(0, transactionService.retrieveGardenTips(sarahsGarden).size(), "All tip transactions should be consumed by claim-tips post");

        updateUsers();
        Assertions.assertEquals(initialBalanceSarah + individualTips, sarah.getBloomBalance(), "Only one tip should be added to Sarah's balance");

        claimTips(liamsGarden, liam);
        Assertions.assertEquals(0, transactionService.retrieveGardenTips(sarahsGarden).size(), "All tip transactions should be consumed by claim-tips post");

        updateUsers();
        Assertions.assertEquals(initialBalanceLiam + individualTips - individualTips, liam.getBloomBalance(), "Only one tip should be added to Liam's balance");
    }

    @Test
    @WithMockUser
    void claimTipsFromOneGarden_TwoSeparateGardensHaveBeenTipped_TipIsOnlyPayedFromOneGarden() throws Exception {
        Integer individualTips = 20;
        String individualTipsString = individualTips.toString();
        Integer initialBalanceSarah = sarah.getBloomBalance();

        tipGarden(sarahsGarden, liam, individualTipsString);
        Assertions.assertEquals(1, transactionService.retrieveGardenTips(sarahsGarden).size(), "There should be 1 transaction on sarah'sGarden from the tip from liam");

        tipGarden(sarahs2ndGarden, liam, individualTipsString);
        Assertions.assertEquals(1, transactionService.retrieveGardenTips(sarahs2ndGarden).size(), "There should be 1 transaction on sarahs2ndGarden from the tip from liam");
        Assertions.assertEquals(1, transactionService.retrieveGardenTips(sarahsGarden).size(), "There should still be only 1 transaction on sarahsGarden");

        claimTips(sarahsGarden, sarah);
        Assertions.assertEquals(0, transactionService.retrieveGardenTips(sarahsGarden).size(), "All tip transactions should be consumed by claim-tips post");
        Assertions.assertEquals(1, transactionService.retrieveGardenTips(sarahs2ndGarden).size(), "Tip shouldn't be claimed from sarahs2ndGarden");

        updateUsers();
        Assertions.assertEquals(initialBalanceSarah + individualTips, sarah.getBloomBalance(), "Only one tip should be added to Sarah's balance");
    }

    private void updateUsers() {
        sarah = userRepository.findById(sarah.getUserId()).orElseThrow(() -> new EntityNotFoundException("Receiver not found"));    //Update sarah from database
        liam = userRepository.findById(liam.getUserId()).orElseThrow(() -> new EntityNotFoundException("Receiver not found"));    //Update liam from database
        inaya = userRepository.findById(inaya.getUserId()).orElseThrow(() -> new EntityNotFoundException("Receiver not found"));    //Update inaya from database
    }

    private void tipGarden(Garden tippedGarden, User tippingUser, String individualTipsString) throws Exception{
        Mockito.when(userService.getAuthenticatedUser()).thenReturn(tippingUser);      // 'Login' as tippingUser

        mockMvc.perform(post("/tip-blooms")    // Tip tippedGarden
                .param("gardenID", tippedGarden.getId().toString())
                .param("tipAmount", individualTipsString)
                .with(csrf()));
    }

    private void claimTips(Garden tippedGarden, User tipClaimingUser) throws Exception {
        Mockito.when(userService.getAuthenticatedUser()).thenReturn(tipClaimingUser);      // 'Login' as tipClaimingUser
        mockMvc.perform(post("/claim-tips").param("gardenID", tippedGarden.getId().toString()).with(csrf()));
    }
}
