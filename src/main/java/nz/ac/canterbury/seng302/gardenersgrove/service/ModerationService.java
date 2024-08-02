package nz.ac.canterbury.seng302.gardenersgrove.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.microsoft.azure.cognitiveservices.vision.contentmoderator.*;
import com.microsoft.azure.cognitiveservices.vision.contentmoderator.models.*;

@Service
public class ModerationService {
    @Value("${azure.api.key:#{null}}")
    private String moderatorApiKey;

    @Value("${azure.api.url:#{null}}")
    private String moderatorApiUrl;

    private static final Logger logger = LoggerFactory.getLogger(ModerationService.class);

    private ContentModeratorClient client;

    @Autowired
    public ModerationService(@Value("${azure.api.url}") String moderatorApiUrl,
                             @Value("${azure.api.key}") String moderatorApiKey) {
        this.moderatorApiUrl = moderatorApiUrl;
        this.moderatorApiKey = moderatorApiKey;
        this.client = ContentModeratorManager.authenticate(AzureRegionBaseUrl.fromString(moderatorApiUrl), moderatorApiKey);
    }

    /**
     * Moderates the given text using Azure Content Moderator.
     *
     * @param line The text to be moderated.
     * @return JSON string containing detected terms if any inappropriate content is found, "null" otherwise.
     */
    public String moderateText(String line) {
        try {
            // Re-authenticate the client
            client = ContentModeratorManager.authenticate(AzureRegionBaseUrl.fromString(moderatorApiUrl), moderatorApiKey);

            Screen textResults = null;
            // For formatting the printed results
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            if (!line.isEmpty()) {
                textResults = client.textModerations().screenText("text/plain", line.getBytes(), null);
            }
            return objectMapper.writeValueAsString(textResults.terms());

        } catch (Exception e) {
            logger.error("Error during text moderation: ", e);
        }
        return line;
    }
}
