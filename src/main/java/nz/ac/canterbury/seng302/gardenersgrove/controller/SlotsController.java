package nz.ac.canterbury.seng302.gardenersgrove.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SlotsController {

    private static int COL_LENGTH = 15;

    private static int NUM_COLS = 5;

    private static int GAME_ROWS = 3;

    // Define the win amounts for numbers 1 to 5
    private static final int WIN_1 = 1;
    private static final int WIN_2 = 2;
    private static final int WIN_3 = 3;
    private static final int WIN_4 = 4;
    private static final int WIN_5 = 5;

    // Define the weights for numbers 1 to 5
    private static final int WEIGHT_1 = 1;
    private static final int WEIGHT_2 = 1;
    private static final int WEIGHT_3 = 1;
    private static final int WEIGHT_4 = 1;
    private static final int WEIGHT_5 = 1;

    private static final int TOTAL_WEIGHT = WEIGHT_1 + WEIGHT_2 + WEIGHT_3 + WEIGHT_4 + WEIGHT_5;

    private static int[] cumulativeWeights = {
            WEIGHT_1,
            WEIGHT_1 + WEIGHT_2,
            WEIGHT_1 + WEIGHT_2 + WEIGHT_3,
            WEIGHT_1 + WEIGHT_2 + WEIGHT_3 + WEIGHT_4,
            WEIGHT_1 + WEIGHT_2 + WEIGHT_3 + WEIGHT_4 + WEIGHT_5
    };

    /**
     * Selects a random number between 1 and 5 weighted by the global constants
     * @return The chosen number as an int
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
        Random random = new Random();
        int[] row = new int[COL_LENGTH];
        for (int i = 0; i < COL_LENGTH; i++) {
            row[i] = getRandomWeighted();
        }
        return row;
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


    private static int getWinAmount(int value) {
        switch (value) {
            case 1: return WIN_1;
            case 2: return WIN_2;
            case 3: return WIN_3;
            case 4: return WIN_4;
            case 5: return WIN_5;
            default: return 0;
        }
    }

    /**
     * Calculates the amount won by the player
     * @param slots The slots returned from generateSlots() function
     * @return The amount won by the player as a number
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
                amountWon += getWinAmount(value);
            }
        }
        return amountWon;
    }

}
