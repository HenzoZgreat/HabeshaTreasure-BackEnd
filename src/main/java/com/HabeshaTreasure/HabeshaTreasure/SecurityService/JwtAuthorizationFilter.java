package com.HabeshaTreasure.HabeshaTreasure.SecurityService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Extract the Authorization header
        String authorizationHeader = request.getHeader("Authorization");

        // Check if the header is present and starts with "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Extract the token by removing the "Bearer " prefix
            String token = authorizationHeader.substring(7);

            try {
                // Extract the email from the token
                String email = jwtUtil.extractEmail(token);
                System.out.println("Extracted email from token: " + email);

                // If the email is not null and there's no existing authentication in the SecurityContext
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Load user details from the database
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    System.out.println("User details loaded from database: " + userDetails.getUsername());
                    // Validate the token
                    if (jwtUtil.validateToken(token, userDetails)) {
                        // Create an authentication object
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        // Set additional details (e.g., IP address, session ID)
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // Set the authentication in the SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            } catch (Exception e) {
                // Log or handle exceptions related to token validation
                System.out.println("Error validating token: " + e.getMessage());
            }
        }
        else
        {
            // Log or handle the case where the token is missing or invalid
            System.out.println("Authorization header is missing or invalid");
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}