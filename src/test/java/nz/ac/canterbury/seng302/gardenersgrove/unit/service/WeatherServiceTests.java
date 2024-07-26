package nz.ac.canterbury.seng302.gardenersgrove.unit.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.WeatherResponse;
import nz.ac.canterbury.seng302.gardenersgrove.service.CountryCodeService;
import nz.ac.canterbury.seng302.gardenersgrove.service.WeatherService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.any;

@ActiveProfiles("test")
public class WeatherServiceTests {
    private WeatherService weatherService;
    private CountryCodeService countryCodeService;
    private String city;
    private String country;


    @BeforeEach
    public void setUp() {
        countryCodeService = Mockito.mock(CountryCodeService.class);
        weatherService = new WeatherService(countryCodeService);

        //test location for BlueSkyWeatherjson
        String country = "NZ";
        String city = "Auckland";
    }

    @Test
    public void NoApiKey_GetCurrentWeather_ReturnsNull() {
        Mockito.when(countryCodeService.getCountryCode(any(String.class))).thenReturn("");
        WeatherResponse weatherServiceResponse = weatherService.getCurrentWeather("", "");
        Assertions.assertNull(weatherServiceResponse);
    }
}
