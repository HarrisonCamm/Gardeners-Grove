package nz.ac.canterbury.seng302.gardenersgrove.advice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = {UserController.class})
public class GlobalControllerAdvice {

    private final BalanceService balanceService;

    @Autowired
    public GlobalControllerAdvice(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @ModelAttribute("balance")
    public Integer getBalance() {
        return balanceService.getUserBalance();
    }
}