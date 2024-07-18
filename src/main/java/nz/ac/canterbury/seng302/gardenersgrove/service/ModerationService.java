package nz.ac.canterbury.seng302.gardenersgrove.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.*;
import com.microsoft.azure.cognitiveservices.vision.contentmoderator.*;
import com.microsoft.azure.cognitiveservices.vision.contentmoderator.models.*;

@Service
public class ModerationService {
    @Value("${azure.api.key}")
    private String moderatorApiKey;

    @Value("${azure.api.url}")
    private String moderatorApiUrl;

    Logger logger = LoggerFactory.getLogger(ModerationService.class);


    //authenticating the client
    ContentModeratorClient client = ContentModeratorManager.authenticate(AzureRegionBaseUrl.fromString(moderatorApiUrl),
            moderatorApiKey);

    public void moderateText(ContentModeratorClient client) {
        logger.info("---------------------------------------");
        logger.info("MODERATE TEXT");

        try {
            String line = "fuck shit crap damn dang hello whoops okay";
            Screen textResults = null;
            // For formatting the printed results
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            while ((line != null)) {
                if (line.length() > 0) {
                    textResults = client.textModerations().screenText("text/plain", line.getBytes(), null);
                    // Uncomment below line to print in console
                    logger.info(gson.toJson(textResults).toString());
                }
            }

            logger.info("Text moderation status: " + textResults.status().description());

//
//            // Create output results file to TextModerationOutput.json
//            BufferedWriter writer = new BufferedWriter(
//                    new FileWriter(new File("src\\main\\resources\\TextModerationOutput.json")));
//            writer.write(gson.toJson(textResults).toString());
//            System.out.println("Check TextModerationOutput.json to see printed results.");
//            System.out.println();
//            writer.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
