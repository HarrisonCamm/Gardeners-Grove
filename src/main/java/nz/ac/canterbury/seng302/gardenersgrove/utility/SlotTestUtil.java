package nz.ac.canterbury.seng302.gardenersgrove.utility;

import nz.ac.canterbury.seng302.gardenersgrove.service.SlotsService;

import java.util.ArrayList;

public class SlotTestUtil {

    private static final int INITIAL_BALANCE = 500;
    private static final int SPIN_COST = 50;
    private static final int SPIN_100K = 100000;
    private static final int SPIN_100 = 100;
    private static final int SPIN_1_MILLION = 1000000;

    int balance = INITIAL_BALANCE;

    public void setUp() {
        balance = INITIAL_BALANCE;
    }

    public void testSpin100k() {
        for (int i = 0; i < SPIN_100K; i++) {
            balance -= SPIN_COST;
            balance += SlotsService.amountWon(SlotsService.generateSlots());
        }
        System.out.println(balance);
    }

    public void testSpin100() {
        ArrayList<Integer> balances = new ArrayList<>();
        for (int j = 0; j < SPIN_100; j++) {
            for (int i = 0; i < SPIN_100; i++) {
                if (balance < SPIN_COST) {
                    break;
                }
                balance -= SPIN_COST;
                balance += SlotsService.amountWon(SlotsService.generateSlots());
            }
            balances.add(balance);
            balance = INITIAL_BALANCE;
        }
        for (Integer amt : balances) {
            System.out.println(amt);
        }
    }

    public void testSpin1Mil() {
        for (int i = 0; i < SPIN_1_MILLION; i++) {
            balance -= SPIN_COST;
            balance += SlotsService.amountWon(SlotsService.generateSlots());
        }
        System.out.println(balance);
    }
}
