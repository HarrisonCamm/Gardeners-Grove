package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.service.AutocompleteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.http.HttpResponse;

@Controller
public class AutocompleteController {

    private static long lastRequestTime = 0;
    private static final long DEBOUNCE_DELAY_MILLIS = 1000; // Adjust debounce delay as needed

    private final AutocompleteService autocompleteService;

    @Autowired
    public AutocompleteController(AutocompleteService autocompleteService) {
        this.autocompleteService = autocompleteService;
    }

    @GetMapping(value = "/getAutocompleteResults", produces = "application/json")
    public ResponseEntity<String> getAutocompleteResults(@RequestParam String inputString) throws IOException, InterruptedException {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRequestTime >= DEBOUNCE_DELAY_MILLIS) {
            // Satisfies debounce delay, proceed with the API call
            lastRequestTime = currentTime;
            HttpResponse<String> response = autocompleteService.getApiResults(inputString);
            return ResponseEntity.status(response.statusCode()).body(response.body());
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(""); //Ok but an empty body
        }
    }
}
