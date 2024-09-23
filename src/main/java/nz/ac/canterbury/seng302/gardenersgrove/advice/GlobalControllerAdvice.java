package nz.ac.canterbury.seng302.gardenersgrove.advice;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Image;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserService userService;

    @ModelAttribute("bloomBalance")
    public Integer getBloomBalance() {
        User authenticatedUser = userService.getAuthenticatedUser();
        if (authenticatedUser != null) {
            return authenticatedUser.getBloomBalance();
        }
        else {
            return null;
        }
    }

}