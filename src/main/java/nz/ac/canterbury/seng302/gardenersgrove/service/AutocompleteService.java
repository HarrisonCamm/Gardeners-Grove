package nz.ac.canterbury.seng302.gardenersgrove.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class AutocompleteService {

    Logger logger = LoggerFactory.getLogger(AutocompleteService.class);
    private final String LIMIT = "5";

    @Value("${geoapify.api.key}")
    private String apiKey;

    public HttpResponse<String> getApiResults(String inputText) throws IOException, InterruptedException {
        HttpResponse<String> response;
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            String url = "https://api.geoapify.com/v1/geocode/autocomplete";
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("text", inputText)
                    .queryParam("format", "json")
                    .queryParam("limit", LIMIT)
                    .queryParam("apiKey", apiKey);

            logger.info("API URL: " + builder.toUriString());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(builder.toUriString()))
                    .header("Content-Type", "application/json")
                    .build();
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        }
        return response;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public int getRequestBodyLength(String inputText) throws IOException, InterruptedException {
        HttpResponse<String> response = getApiResults(inputText);
        return response.body().length();
    }
}
