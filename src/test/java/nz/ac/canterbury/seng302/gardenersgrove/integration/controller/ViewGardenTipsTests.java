package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.TransactionRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("cucumber")
@SpringBootTest
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

    @BeforeEach
    public void setUp() {
        sarah = userRepository.save(new User("","sarah",null,null,null));
        inaya = userRepository.save(new User("","inaya",null,null,null));
        liam = userRepository.save(new User("","liam",null,null,null));
    }

    @Test
    public void claimTips_withOneTipTransaction_Success() {
        User user = new User();
        User savedUser = userRepository.save(user);
        Garden garden = new Garden("Garden name",new Location(), "23", savedUser, "This is a descriptions");
        Garden retreivedGarden = gardenService.addGarden(garden);
        userService.addUser(user);
        transactionService.addTransaction(10, "test","testtranstype", savedUser.getUserId(), savedUser.getUserId(),false, garden);
        System.out.println(transactionService.retrieveGardenTips(retreivedGarden));
    }


}
