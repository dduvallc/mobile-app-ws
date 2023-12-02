package com.dduvall.developerblog.apps.ws.security;

import com.dduvall.developerblog.apps.ws.SpringApplicationContext;
import com.dduvall.developerblog.apps.ws.service.UserService;
import com.dduvall.developerblog.apps.ws.shared.dto.UserDto;
import com.dduvall.developerblog.apps.ws.ui.model.request.UserLoginRequestModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

// used to authenticate user when they send the request to perform user login

// So when our application receives an HTTP request to perform user login, this filter will trigger.
// It will read username and password from HTTP request, and it will pass this username and password on
// to the spring framework.
// Spring framework will validate the provided user credentials and if they are correct, it will handle
// the control back to us so that we can generate access token.
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {  // UsernamePass... class processes authentication information
                                                                                  // when it is submitted in HTTP request.
    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    } // AuthenticationManager &UsernamePasswordAuthenticationFilter provide by SF and just need to use them correctly

    // method part of the UsernamePasswordAuthenticationFilter class (part of the default filter chain).
    // When login request, then HTTP request will pass through this filter
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        try {
            // use HTTP request object to read JSON payload, map into object of user.
            UserLoginRequestModel creds = new ObjectMapper().readValue(req.getInputStream(), UserLoginRequestModel.class);

            // use UserLoginRequestModel to read username & passwd and pass to authenticate method
            // And it is after we invoke this authenticate method, that spring framework will invoke the loadUserByUsername
            // method, so SF will try to locate user details by calling loadUserByUsername
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(), new ArrayList<>()));

        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        // Use my security constants class to read the value of token secret and get its bytes.
        // Then use base64 encoder to encode these bytes into a new byte array.
        byte[] secretKeyBytes = Base64.getEncoder().encode(SecurityConstants.getTokenSecret().getBytes());

        //use this byte array to generate secret key which will be used to sign JWT access token in the code below
        SecretKey secretKey = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS512.getJcaName());

        Instant now = Instant.now(); // get current time

        String userName = ((User) auth.getPrincipal()).getUsername(); //use auth object to read username of the currently authenticated user and then add
                                                                      // this username as a subject to access token below
        String token = Jwts.builder()  //create token
                .setSubject(userName)
                .setExpiration(
                        Date.from(now.plusMillis(SecurityConstants.EXPIRATION_TIME))) // set token expiration date
                .setIssuedAt(Date.from(now)).signWith(secretKey, SignatureAlgorithm.HS512).compact(); // set time when token issued, sign with secret key
                                                                                                      // and compact method will return final value of JWT token

        // calls helper class to get a bean where we need one.
        UserService userService = (UserService) SpringApplicationContext.getBean("userServiceImpl");
        UserDto userDto = userService.getUser(userName);

        // when client application receives a HTTP response, it will be able to read this JWT access token
        res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token); // add as a header to response object
        res.addHeader("UserId", userDto.getUserId());

    }

}
