package nz.ac.canterbury.seng302.gardenersgrove.unit.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.SlotsService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class SlotsServiceTest {

    private static SlotsService slotsService;

    @Mock
    private static User user;

    @BeforeAll
    public static void setUp() {
        slotsService = new SlotsService();
        user = mock(User.class);
        // Mocking user behavior
        when(user.getBloomBalance()).thenReturn(100);
        doNothing().when(user).setBloomBalance(anyInt());
    }

    @Test
    public void testGenerateSlots_ReturnsCorrectNumberOfSlots() {
        List<int[]> result = slotsService.generateSlots();

        assertNotNull(result, "Generated slots should not be null.");
        assertEquals(5, result.size(), "The size of generated slots is incorrect.");
    }


}
