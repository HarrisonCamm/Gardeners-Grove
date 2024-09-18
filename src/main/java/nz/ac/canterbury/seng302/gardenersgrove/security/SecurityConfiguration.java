package nz.ac.canterbury.seng302.gardenersgrove.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
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
        http
                .authorizeHttpRequests(auth -> auth
                        // Permit WebSocket connections
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/h2/**")).permitAll()
                        .requestMatchers("/", "/register-form", SIGN_IN_FORM, "/home", "/confirm-registration", "/lost-password-form", "reset-password-form", "/tag", "/dismiss-alert")
                        .permitAll()
                        .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**", "/javascript/**")
                        .permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .requestMatchers("/", "/register-form", "/sign-in-form", "/home", "/confirm-registration").hasRole("UNVERIFIED")
                        .requestMatchers("/main", "/view-user-profile", "/edit-user-profile", "/create-garden", "/view-garden", "/view-gardens", "/create-plant", "/edit-plant", "/upload-image", "/manage-friends", "/add-tag", "/contacts", "/shop").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage(SIGN_IN_FORM)
                        .loginProcessingUrl(SIGN_IN_FORM)
                        .defaultSuccessUrl("/main")
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl(SIGN_IN_FORM)
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .headers(headers -> headers
                        .frameOptions(Customizer.withDefaults())
                        .disable()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2/**"), AntPathRequestMatcher.antMatcher("/ws/**")) // Disable CSRF protection for WebSocket
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.accessDeniedHandler((request, response, accessDeniedException) -> {
                            if (request.isUserInRole("UNVERIFIED")) {
                                response.sendRedirect("/confirm-registration");
                            }
                        })
                );

        return http.build();
    }
}
