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

import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ModerationService {
    @Value("${azure.api.key:#{null}}")
    private String moderatorApiKey;

    @Value("${azure.api.url:#{null}}")
    private String moderatorApiUrl;

    private static final Logger logger = LoggerFactory.getLogger(ModerationService.class);

    private ContentModeratorClient client;

    // Allows only atomic operations, i.e. only 1 thread can edit this variable at a time without conflicts
    // to prevent race conditions. New threads may be spawned by client requests.
    private AtomicBoolean isBusy = new AtomicBoolean(false);

    public ModerationService() {
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
            waitUntilNotBusy();

            // For formatting the printed results
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            if (line.isEmpty())  {
                return "null";
            }


            // Re-authenticate the client
            client = ContentModeratorManager.authenticate(AzureRegionBaseUrl.fromString(moderatorApiUrl), moderatorApiKey);
            Screen textResults = client.textModerations().screenText("text/plain", line.getBytes(), null);

            Status status = textResults.status();

            // Check for evaluation error AC3; status code 3000 means OK, or successful evaluation
            if (!status.code().equals(3000) || !status.description().equals("OK")) {
                return "evaluation_error";
            }
            return objectMapper.writeValueAsString(textResults.terms());

        } catch (Exception e) {
            logger.error("Error during text moderation: ", e);
            return "evaluation_error";
        } finally {
            isBusy.set(false);
        }
    }

    /**
     * Waits in a loop until the azure API isn't being called (in this running instance), to prevent concurrent calls.
     */
    private void waitUntilNotBusy() {
        while (!isBusy.compareAndSet(false, true)) {
            // Busy-wait
        }
    }

    // If the tag is appropriate returns true, otherwise false
    public boolean isContentAppropriate(String content) {
        String moderatedContent = moderateText(content);

        // Return true if no terms are found, indicating the content is appropriate
        return moderatedContent == null || moderatedContent.equals("null");
    }

    public boolean isBusy() {
        return isBusy.get();
    }
}
