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
    private ContentModeratorClient client = ContentModeratorManager.authenticate(AzureRegionBaseUrl.fromString(moderatorApiUrl),
            moderatorApiKey);

    //below is adapted from microsoft quickstart
    public void moderateText(String line) {
        logger.info("---------------------------------------");
        logger.info("MODERATE TEXT");

        try {
            client = ContentModeratorManager.authenticate(AzureRegionBaseUrl.fromString(moderatorApiUrl),
                    moderatorApiKey);

            Screen textResults = null;
            // For formatting the printed results
            Gson gson = new GsonBuilder().setPrettyPrinting().create();


            if (line.length() > 0) {
                textResults = client.textModerations().screenText("text/plain", line.getBytes(), null);
                // Uncomment below line to print in console
                logger.info(gson.toJson(textResults).toString());
            }


            logger.info("Text moderation status: " + textResults.status().description());
//            logger.info(textResults.classification().toString()); // todo fix this to retrieve the classification

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
