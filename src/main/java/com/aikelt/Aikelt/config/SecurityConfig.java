package com.aikelt.Aikelt.config;

import com.aikelt.Aikelt.security.JwtAuthFilter;
import com.aikelt.Aikelt.security.JwtService;
import com.aikelt.Aikelt.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtService jwtService, CustomUserDetailsService userService) throws Exception {

        http
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                // Allow unauthenticated access to /api/auth/login
                                .requestMatchers("/api/auth/login").permitAll()
                                // Protect all other /api/** endpoints (authentication required)
                                .requestMatchers("/api/**").authenticated()
                                // All other requests require authentication
                                .requestMatchers("/h2-ui/**").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .permitAll()  // Make the login page publicly accessible
                )
                .logout(logout -> logout.permitAll())  // Allow logout without authentication
                .csrf(csrf -> csrf
                        // Disable CSRF protection for API endpoints (optional)
                        .ignoringRequestMatchers("/api/**")
                        .ignoringRequestMatchers("/h2-ui/**")
                )
                // Add JwtAuthFilter before BasicAuthenticationFilter to process token
                .addFilterBefore(new JwtAuthFilter(jwtService, userService), BasicAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}






