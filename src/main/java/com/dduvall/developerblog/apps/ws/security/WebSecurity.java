package com.dduvall.developerblog.apps.ws.security;

import com.dduvall.developerblog.apps.ws.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurity {

    private final UserService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public WebSecurity(UserService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {

        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        corsConfiguration.setAllowedOrigins(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setExposedHeaders(List.of("Authorization"));

        // Configure AuthenticationManagerBuilder - needed for user authentication process
        // You can think of user authentication as user login feature.
        // To perform user login or user authentication,
        // we will need to send the HTTP request to forward slash login API endpoint
        // and include username and password in the body of HTTP request.
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        // authenticate the password by telling which encoder to use
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)  // telling SF which service class to load user details from DB and....
                .passwordEncoder(bCryptPasswordEncoder); // which encryption object it should use to verify if login request passwd
                                                         // matches the encrypted passwd in the DB
        //Build custom AuthenticationManager object
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        // Customize Login URL path
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager);
        authenticationFilter.setFilterProcessesUrl("/users/login");

        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(new AntPathRequestMatcher(SecurityConstants.SIGN_UP_URL, "POST")) // configured to be public
                        .permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/error", "POST"))  //to receive error message
                        .permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/error", "GET"))  //to receive error message
                        .permitAll()
                        .anyRequest().authenticated()) // only one of .anyRequest...authenticated...
                .authenticationManager(authenticationManager) //update http security object with custom authenticationManager
                .addFilter(authenticationFilter)
                .addFilter(new AuthorizationFilter(authenticationManager))
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();

    }

}

//        AuthenticationManager authenticationManager = authenticationManagerBuilder.build(); //don't need this code now
//        http.authenticationManager(authenticationManager); // register with http security object (don't need code now)

//the above replaces this below due to deprecation
//        http.authorizeHttpRequests().requestMatchers(HttpMethod.POST, "/users").permitAll()
//                .anyRequest().authenticated();