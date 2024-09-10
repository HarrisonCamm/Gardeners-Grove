package nz.ac.canterbury.seng302.gardenersgrove.configuration;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    @Bean
    public Random random() { return new Random(); }

}