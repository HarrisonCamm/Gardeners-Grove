package nz.ac.canterbury.seng302.gardenersgrove.security;

import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Custom Authentication Provider class, to allow for handling authentication in any way we see fit.
 * In this case using our existing {@link User}
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    /**
     * User service for custom authentication using our own user objects
     */
    private UserService userService;

    /**
     *
     * @param userService User service for custom authentication using our own user objects to be injected in
     */
    public CustomAuthenticationProvider(UserService userService) {
        super();
        this.userService = userService;
    }

    /**
     * Custom authentication implementation
     * @param authentication An implementation object that must have non-empty email (name) and password (credentials)
     * @return A new {@link UsernamePasswordAuthenticationToken} if email and password are valid with users authorities
     */
    @Override
    public Authentication authenticate(Authentication authentication) {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            throw new BadCredentialsException("Bad Credentials");
        }

        Optional<User> optionalUser = userService.validateUser(email, password);
        if (optionalUser.isEmpty()) {
            throw new BadCredentialsException("Invalid username or password");
        }

        User user = optionalUser.get();
        return new UsernamePasswordAuthenticationToken(user.getEmail(), null, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
