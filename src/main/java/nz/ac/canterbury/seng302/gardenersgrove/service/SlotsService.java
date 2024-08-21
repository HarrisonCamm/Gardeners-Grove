package nz.ac.canterbury.seng302.gardenersgrove.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SlotsService {

    private final static int  COL_LENGTH = 15;

    private final static int NUM_COLS = 5;

    private final static int GAME_ROWS = 3;

    // Define the win amounts for numbers 1 to 5
    private static final int WIN_1 = 20;
    private static final int WIN_2 = 50;
    private static final int WIN_3 = 100;
    private static final int WIN_4 = 500;
    private static final int WIN_5 = 1000;

    //IMPORTANT NOT 0 INDEXED
    private static final int[] WIN_AMOUNTS = {0, WIN_1, WIN_2, WIN_3, WIN_4, WIN_5};

    // Define the weights for numbers 1 to 5
    private static final int WEIGHT_1 = 20;
    private static final int WEIGHT_2 = 5;
    private static final int WEIGHT_3 = 3;
    private static final int WEIGHT_4 = 2;
    private static final int WEIGHT_5 = 1;

    private static final int TOTAL_WEIGHT = WEIGHT_1 + WEIGHT_2 + WEIGHT_3 + WEIGHT_4 + WEIGHT_5;

    private final static int[] cumulativeWeights = {
            WEIGHT_1,
            WEIGHT_1 + WEIGHT_2,
            WEIGHT_1 + WEIGHT_2 + WEIGHT_3,
            WEIGHT_1 + WEIGHT_2 + WEIGHT_3 + WEIGHT_4,
            WEIGHT_1 + WEIGHT_2 + WEIGHT_3 + WEIGHT_4 + WEIGHT_5
    };

    /**
     * Randomly assigns which emoji will be added to the slot machine column
     * (Chooses one of the 5 emojis randomly)
     * Selects a random number between 1 and 5 based on predefined weights (odds).
     * This number represents one of the 5 emojis used in the slot machine
     * ["" ,"💧", "☀️", "🍄", "🌶️", "🌾"];
     * The odds are calculated by adding all the weightings of each emoji together
     * Then the emoji is chosen based on which emojis range of the cumulative weighting the random int lands in
     * @return The chosen number (slot machine emoji) as an int, between 1 and 5.
     */
    private static int getRandomWeighted() {
        Random random = new Random();
        int randomValue = random.nextInt(TOTAL_WEIGHT) + 1;

        if (randomValue <= cumulativeWeights[0]) return 1;
        if (randomValue <= cumulativeWeights[1]) return 2;
        if (randomValue <= cumulativeWeights[2]) return 3;
        if (randomValue <= cumulativeWeights[3]) return 4;
        return 5;
    }

    /**
     * Generates a column of the slot machine, a list of numbers selected by getRandomWeighted()
     * Size is defined by COL_LENGTH
     * The column is every number (emoji) that column of the slot machine will spin through
     * @return  int[] of random numbers
     */
    private static int[] generateColumn() {
        int[] col = new int[COL_LENGTH];
        for (int i = 0; i < COL_LENGTH; i++) {
            col[i] = getRandomWeighted();
        }
        return col;
    }

    /**
     *Generates all columns of the slot machine
     * Each column contains every single number (emoji) that column of the slot machine will spin through
     * @return List of lists, of int[] arrays representing the slot machine
     */
    public static List<int[]> generateSlots() {
        List<int[]> slots = new ArrayList<>();
        for (int i = 0; i < NUM_COLS; i++) {
            slots.add(generateColumn());
        }
        return slots;
    }


    /**
     * Calculates the amount won by the player
     * @param slots The slots returned from generateSlots() function
     * @return The amount won by the player as a number 😊
     */
    public static int amountWon(List<int[]> slots) {
        int amountWon = 0;
        for (int i = COL_LENGTH - 1; i >= COL_LENGTH - GAME_ROWS; i--) {
            int value = slots.get(0)[i];
            boolean isWin = true;
            for (int j = 1; j < slots.size(); j++) {
                if (slots.get(j)[i] != value) {
                    isWin = false;
                    break;
                }
            }
            if (isWin) {
                amountWon += WIN_AMOUNTS[value];
            }
        }
        return amountWon;
    }

}
