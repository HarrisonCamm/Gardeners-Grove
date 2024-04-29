package nz.ac.canterbury.seng302.gardenersgrove.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Custom Security Configuration
 * Such functionality was previously handled by WebSecurityConfigurerAdapter
 */
@Configuration
@EnableWebSecurity
// don't worry if the "com.baeldung.security" comes up red in IntelliJ
@ComponentScan("com.baeldung.security")
public class SecurityConfiguration {
    private static final String SIGN_IN_FORM = "/sign-in-form";

    /**
     * Our Custom Authentication Provider {@link CustomAuthenticationProvider}
     */
    private final CustomAuthenticationProvider authProvider;

    /**
     *
     * @param authProvider Our Custom Authentication Provider {@link CustomAuthenticationProvider} to be injected in
     */
    public SecurityConfiguration(CustomAuthenticationProvider authProvider) {
        this.authProvider = authProvider;
    }

    /**
     * Create an Authentication Manager with our {@link CustomAuthenticationProvider}
     * @param http http security configuration object from Spring
     * @return a new authentication manager
     * @throws Exception if the AuthenticationManager can not be built
     */
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authProvider);
        return authenticationManagerBuilder.build();

    }

    /**
     *
     * @param http http security configuration object from Spring (beaned in)
     * @return Custom SecurityFilterChain
     * @throws Exception if the SecurityFilterChain can not be built
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth.requestMatchers(AntPathRequestMatcher.antMatcher("/h2/**")).permitAll())
                .headers(headers -> headers.frameOptions(Customizer.withDefaults()).disable())
                .csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2/**")))

                .authorizeHttpRequests(request ->
                    // Allow "/", "/register", and "/login" to anyone (permitAll)
                    // Authenticated and non-Authenticated users can access these pages
                    request.requestMatchers("/", "/register-form", SIGN_IN_FORM, "/home", "/confirm-registration")
                    .permitAll()
                    // Could change .permitAll() to .anonymous() to give access to these pages only to non-Authenticated users

                    // Allow static resources to be accessed by anyone
                    .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**")
                    .permitAll()

                    // Only allow admins to reach the "/admin" page
                    .requestMatchers("/admin")
                    .hasRole("ADMIN")

                    // Only allow unverified users to reach the "/confirm-registration" page
                    .requestMatchers("/", "/register-form", "/sign-in-form", "/home", "/confirm-registration")
                    .hasRole("UNVERIFIED")

                    // Increase access to authenticated users to reach the "/main", "/view-user-profile", "/edit-user-profile" pages
                    .requestMatchers("/main", "/view-user-profile", "/edit-user-profile", "/create-garden", "/view-garden", "/view-gardens", "/create-plant", "/edit-plant")
                    .hasRole("USER")

                    // Any other request requires authentication
                    .anyRequest()
                    .authenticated()
                )
                // Define logging in, a POST "/login" endpoint now exists under the hood, after login redirect to user page
                .formLogin(formLogin -> formLogin.loginPage(SIGN_IN_FORM).loginProcessingUrl(SIGN_IN_FORM).defaultSuccessUrl("/main"))
                // Define logging out, a POST "/logout" endpoint now exists under the hood, redirect to "/login", invalidate session and remove cookie
                .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl(SIGN_IN_FORM).invalidateHttpSession(true).deleteCookies("JSESSIONID"));
                //ChatGPT code to redirect unverified user to confirm registration page if they enter a url they don't have permission for
                http.exceptionHandling().accessDeniedHandler((request, response, accessDeniedException) -> {
                    if (request.isUserInRole("UNVERIFIED")) {
                        response.sendRedirect("/confirm-registration");
                    }
                });
        return http.build();
    }
}
