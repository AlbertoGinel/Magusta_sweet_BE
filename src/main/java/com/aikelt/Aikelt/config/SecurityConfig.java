package com.aikelt.Aikelt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(@Qualifier("customUserDetailsService") UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize  // Authorize HTTP requests
                        .requestMatchers("/api/auth/login", "/h2-ui/**","/api/users/**").permitAll()  // Allow unauthenticated access to login endpoint and h2-console
                        .anyRequest().authenticated()  // All other requests must be authenticated
                )
                .formLogin(formLogin -> formLogin  // Custom form login configuration
                        .loginPage("/login")  // Custom login page for form login
                        .permitAll()  // Allow unauthenticated access to the login page
                )
                .logout(LogoutConfigurer::permitAll)  // Allow anyone to log out
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/auth/login")  // Disable CSRF protection for login endpoint (this is typical for APIs)
                        .ignoringRequestMatchers("/h2-ui/**")
                        .ignoringRequestMatchers("/api/users/**") // Disable CSRF protection for the H2 console
                )
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("frame-ancestors 'self'")  // Allow H2 console to be embedded in iframe (same-origin)
                        )
                );

        return http.build();  // Return the configured SecurityFilterChain to Spring
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {

        System.out.println("\u001B[33m we are in AuthenticationManager \u001B[0m");

        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        System.out.println("\u001B[33m we are in DaoAuthenticationProvider \u001B[0m");

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder()); // Provide a password encoder bean as well
        return authProvider;
    }

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }
}