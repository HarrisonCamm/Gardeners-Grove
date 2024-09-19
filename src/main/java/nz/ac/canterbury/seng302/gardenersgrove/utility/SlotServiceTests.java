package nz.ac.canterbury.seng302.gardenersgrove.utility;

import nz.ac.canterbury.seng302.gardenersgrove.service.SlotsService;

import java.util.ArrayList;

public class SlotServiceTests {

    int balance = 500;
    int spinCost = 50;

    public void setUp() {
        balance = 500;
    }

    public void testSpin100k() {
        for (int i = 0; i < 100000; i++) {
            balance -= spinCost;
            balance += SlotsService.amountWon(SlotsService.generateSlots());
        }
        System.out.println(balance);
    }

    public void testSpin100() {
        ArrayList<Integer> balances = new ArrayList();
        for (int j = 0; j < 100; j++) {
            for (int i = 0; i < 100; i++) {
                if (balance < 50) {
                    break;
                }
                balance -= spinCost;
                balance += SlotsService.amountWon(SlotsService.generateSlots());

            }
            balances.add(balance);
            balance = 500;
        }
        for (Integer amt : balances) {
            System.out.println(amt);
        }
    }

    public void testSpin1Mil() {
        for (int i = 0; i < 1000000; i++) {
            balance -= spinCost;
            balance += SlotsService.amountWon(SlotsService.generateSlots());
        }
        System.out.println(balance);
    }
}
