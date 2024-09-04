package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.UserProfileController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Transaction;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.ImageService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserProfileController.class)
public class UserProfileTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ImageService imageService;

    @BeforeEach
    public void setUp() {
        Mockito.reset(userService, userRepository);
    }

    /**
     * Test the successful fetch scenario where valid user data is retrieved.
     */
    @Test
    @WithMockUser
    public void whenGetUserProfileWithAuthenticatedUser_thenReturnsUserProfileView() throws Exception {
        User mockUser = new User("user@email.com", "User", "Name", "password");
        mockUser.setDateOfBirth("01/01/1990");

        // Create mock Users
        User sender = new User("sender@email.com", "Sender", "One", "password");
        sender.setDateOfBirth("01/01/1980");

        User receiver = new User("receiver@email.com", "Receiver", "Two", "password");
        receiver.setDateOfBirth("01/01/1990");


        // Mock the transaction page
        // Create a list of transactions
        List<Transaction> transactions = new ArrayList<Transaction>();


        // Add some transactions
        transactions.add(new Transaction(null, receiver, 50, new Date(), "tip", null, "Tip for excellent work."));
        transactions.add(new Transaction(sender, receiver, 200, new Date(), "reward", null, "Reward for garden competition."));


        Page<Transaction> transactionsPage = ;


        // Mock the user service methods
        when(userService.getAuthenticatedUser()).thenReturn(mockUser);
        when(userService.findTransactionsByUser(mockUser, 0, 10)).thenReturn(transactionsPage);  // Mocking page 0, size 10

        // Perform the request and verify the response
        mockMvc.perform(get("/view-user-profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("viewUserProfileTemplate"))
                .andExpect(model().attribute("displayName", "User Name"))
                .andExpect(model().attribute("email", "user@email.com"))
                .andExpect(model().attribute("dateOfBirth", "01/01/1990"))
                .andExpect(model().attribute("transactions", transactionsPage.getContent()))  // Ensure the transactions are passed correctly
                .andExpect(model().attribute("totalPages", transactionsPage.getTotalPages()))
                .andExpect(model().attribute("currentPage", 0))
                .andExpect(model().attribute("pageSize", 10))
                .andExpect(model().attribute("hasPrevious", transactionsPage.hasPrevious()))
                .andExpect(model().attribute("hasNext", transactionsPage.hasNext()));

    }
}