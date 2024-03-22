package nz.ac.canterbury.seng302.gardenersgrove.unit;

import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService.addEndpoint;
import static nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService.getPreviousPage;
import static org.junit.jupiter.api.Assertions.*;

public class RedirectServiceTest {


    @BeforeEach
    public void clearArray() {
        RedirectService.getUrlEndpoints().clear();
    }
    @Test
    public void EmptyArray_GetPreviousPageEndPoint_ReturnsRoot() {
        assertEquals("/", getPreviousPage());
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 10, 50, 100})
    public void NonEmptyArray_GetPreviousPageEndPoint_ReturnsNotNull(int times) {
        for (int i = 0; i < times; i++) {
            addEndpoint("/endpoint");
        }
        assertNotNull(getPreviousPage());
    }
}
