package com.dduvall.developerblog.apps.ws.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

// BasicAuthenticationFiler class, is a class in spring security that processes HTTP requests
// with basic authorization headers and then puts the result into spring security context.
public class AuthorizationFilter extends BasicAuthenticationFilter {

    public AuthorizationFilter(AuthenticationManager authManager) {
        super(authManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String header = request.getHeader(SecurityConstants.HEADER_STRING);

        if (header == null || !header.startsWith((SecurityConstants.TOKEN_PREFIX))) {
            chain.doFilter(request, response);   // pass to next filter in chain
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);  // import UsernamePasswordAuthenticationToken object
        SecurityContextHolder.getContext().setAuthentication(authentication); // need to put above object into SecurityContextHolder
        chain.doFilter(request, response); // pass execution to the next filter in the chain
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {

        String authorizationHeader = request.getHeader(SecurityConstants.HEADER_STRING); // read header

        if (authorizationHeader == null) {  // check if null
            return null;
        }

        String token = authorizationHeader.replace(SecurityConstants.TOKEN_PREFIX, ""); // remove 'Bearer' prefix

        // In the next two lines I prepare a secret key using the same value of token secret that I have hardcoded
        // in the security constants.
        byte[] secretKeyBytes = Base64.getEncoder().encode(SecurityConstants.getTokenSecret().getBytes());
        SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyBytes);

        JwtParser parser = Jwts.parser()
                .verifyWith(secretKey)
                .build();

        String subject = parser.parseSignedClaims(token).getPayload().getSubject();

        if (subject == null) {
            return null;
        }

        return new UsernamePasswordAuthenticationToken(subject, null, new ArrayList<>());

    }

}
