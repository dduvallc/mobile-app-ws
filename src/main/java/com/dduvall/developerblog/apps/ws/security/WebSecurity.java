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

        // Configure AuthenticationManagerBuilder
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder);

        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        // Customize Login URL path
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager);
        authenticationFilter.setFilterProcessesUrl("/users/login");

        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL, "/error")  //to receive error message
                        .permitAll()
                        .requestMatchers(HttpMethod.GET,  "/error")  //to receive error message
                        .permitAll()
                        .anyRequest().authenticated()) // only one of .anyRequest...authenticated...
                .authenticationManager(authenticationManager)
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