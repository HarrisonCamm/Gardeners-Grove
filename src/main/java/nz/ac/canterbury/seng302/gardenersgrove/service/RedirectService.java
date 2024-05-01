package nz.ac.canterbury.seng302.gardenersgrove.service;

import java.util.ArrayList;

/**
 * This service is used to add url endpoints so that the previous page can be fetched by controllers
 */
public class RedirectService {

    private static ArrayList<String> urlEndpoints = new ArrayList<>();

    /**
     * Adds an endpoint to the static array in the form of '/endpoint'
     * @param endpoint The endpoint
     */
    public static void addEndpoint(String endpoint) {
        urlEndpoints.add(endpoint);
    }

    /**
     * Gets the previous page, if there is one, and pops it
     * @return The previous endpoint
     */
    public static String getPreviousPage() {
        String prevPage = "/";
        if (!urlEndpoints.isEmpty()) {
            prevPage = urlEndpoints.get(urlEndpoints.size() - 1);
        }
        return prevPage;
    }

    public static ArrayList<String> getUrlEndpoints() { return urlEndpoints; }

}
