package nz.ac.canterbury.seng302.gardenersgrove.integration;

import nz.ac.canterbury.seng302.gardenersgrove.controller.AutocompleteController;
import nz.ac.canterbury.seng302.gardenersgrove.service.AutocompleteService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.LocationService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest
public class AutocompleteControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    AutocompleteService autocompleteService;

    @MockBean
    GardenService gardenService;

    @MockBean
    LocationService locationService;

    @MockBean
    private PlantService PlantService;

    AutocompleteController autocompleteController;

    @BeforeEach
    public void setUp() {
        autocompleteController = new AutocompleteController(autocompleteService);
    }

    @Test
    public void TypedInAddress_ServiceReturns_Ok() throws Exception {

        HttpResponse<String> httpResponse = new HttpResponse<>() {
            @Override
            public int statusCode() {
                return HttpStatus.OK.value();
            }

            @Override
            public HttpRequest request() {
                return null;
            }

            @Override
            public Optional<HttpResponse<String>> previousResponse() {
                return Optional.empty();
            }

            @Override
            public HttpHeaders headers() {
                return null;
            }

            @Override
            public String body() {
                return null;
            }

            @Override
            public Optional<SSLSession> sslSession() {
                return Optional.empty();
            }

            @Override
            public URI uri() {
                return null;
            }

            @Override
            public HttpClient.Version version() {
                return null;
            }
        };

        when(autocompleteService.getApiResults(any(String.class))).thenReturn(httpResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/getAutocompleteResults")
                .param("inputString", "University of Canterbury"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
