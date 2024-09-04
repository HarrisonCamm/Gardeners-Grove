package nz.ac.canterbury.seng302.gardenersgrove.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SlotsService {

    private final static int  COL_LENGTH = 15;

    private final static int NUM_COLS = 5;

    private final static int GAME_ROWS = 3;

    // Define base (3 in a row) amount of blooms for each emoji
//         * ["" ,"💧", "☀️", "🍄", "🌶️", "🌾"];

    private static final int WIN_1 = 1;         // 3 in a row of 💧
    private static final int WIN_2 = 2;        // 3 in a row of ☀️
    private static final int WIN_3 = 3;      // 3 in a row of 🍄
    private static final int WIN_4 = 4;      // 3 in a row of 🌶️
    private static final int WIN_5 = 5;    // 3 in a row of 🌾

    //IMPORTANT NOT 0 INDEXED
    private static final int[] WIN_AMOUNTS = {0, WIN_1, WIN_2, WIN_3, WIN_4, WIN_5};

    //Define Multipliers for base win amounts
    private static final int MULTIPLIER_4_IN_A_ROW = 10;
    private static final int MULTIPLIER_5_IN_A_ROW = 100;

    // Define the weights (odds) of each emoji appearing on the slot machine
    // The higher the weight, the more likely the emoji will appear
    private static final int WEIGHT_1 = 4;
    private static final int WEIGHT_2 = 4;
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
    private static int chooseEmoji() {
        Random random = new Random();
        int randomValue = random.nextInt(TOTAL_WEIGHT) + 1;

        if (randomValue <= cumulativeWeights[0]) return 1;                          // Range of emoji 1, 💧
        if (randomValue <= cumulativeWeights[1]) return 2;                          // Range of emoji 2, ☀️
        if (randomValue <= cumulativeWeights[2]) return 3;                          // Range of emoji 3, 🍄
        if (randomValue <= cumulativeWeights[3]) return 4;                          // Range of emoji 4, 🌶️
        return 5;                                                                                                   // Range of emoji 5, 🌾      (anything greater than lower emoji bounds)
    }

    /**
     * Generates a column of the slot machine, a list of numbers (emojis) selected by chooseEmoji()
     * Size is defined by COL_LENGTH
     * The column is every number (emoji) that column of the slot machine will spin through
     * @return  int[] of random numbers
     */
    private static int[] generateColumn() {
        int[] col = new int[COL_LENGTH];
        for (int i = 0; i < COL_LENGTH; i++) {
            col[i] = chooseEmoji();
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
     * @return The amount of blooms won by the player as a number 😊
     */
    public static int amountWon(List<int[]> slots) {
        int amountWon = 0;

        // Iterate through the last GAME_ROWS rows
        for (int row = COL_LENGTH - GAME_ROWS; row < COL_LENGTH; row++) {
            // Iterate through each column
            for (int col = 0; col < NUM_COLS - 2; col++) { // -2 to avoid out of bounds when checking 3 in a row
                int currentEmoji = slots.get(col)[row];
                int comboLength = 1;

                // Check for matching emojis in the next two columns
                for (int nextCol = col + 1; nextCol < NUM_COLS; nextCol++) {
                    if (slots.get(nextCol)[row] == currentEmoji) {
                        comboLength++;
                    } else {
                        break;
                    }
                }

                // Calculate the win amount based on combo length
                if (comboLength >= 3) {
                    int baseWinAmount = WIN_AMOUNTS[currentEmoji];
                    if (comboLength == 4) {
                        baseWinAmount *= MULTIPLIER_4_IN_A_ROW;
                    } else if (comboLength == 5) {
                        baseWinAmount *= MULTIPLIER_5_IN_A_ROW;
                    }
                    amountWon += baseWinAmount;

                    // Move the column index forward to skip over the already checked combo
                    col += comboLength - 1;
                }
            }
        }

        return amountWon;
    }


}
