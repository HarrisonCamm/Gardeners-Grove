package nz.ac.canterbury.seng302.gardenersgrove.unit.service;

import nz.ac.canterbury.seng302.gardenersgrove.service.AutocompleteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import java.io.IOException;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest //We need the application context to get the API key
public class AutocompleteServiceTests {

    private AutocompleteService autocompleteService;

    @Value("${geoapify.api.key}")
    private String apiKey;

    @BeforeEach
    public void setUp() {
        autocompleteService = new AutocompleteService();
        autocompleteService.setApiKey(apiKey);
    }

    @ParameterizedTest
    @CsvSource({"University of Canterbury", "8 Moorpark Place", "38 Middle Yards Road"})
    public void GettingAPIResults_ValidString_Ok(String inputString) throws IOException, InterruptedException {
        HttpResponse<String> actualResponse = autocompleteService.getApiResults(inputString);

        assertEquals(HttpStatus.OK.value(), actualResponse.statusCode());
    }
}

    //TODO make test to parse the JSON to check if body has 5 elements, and test if bad input has 0 results

