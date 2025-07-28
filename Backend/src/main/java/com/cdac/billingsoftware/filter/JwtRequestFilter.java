package com.cdac.billingsoftware.filter;

import com.cdac.billingsoftware.service.impl.AppUserDetailsService;
import com.cdac.billingsoftware.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Marks this class as a Spring Bean so it can be used as a filter
@RequiredArgsConstructor // Auto-generates a constructor for the final fields
public class JwtRequestFilter extends OncePerRequestFilter {

    // Service to load user details (from DB)
    private final AppUserDetailsService userDetailsService;
    // Utility class to handle JWT operations (extract, validate, etc.)
    private final JwtUtil jwtUtil;

    // This method is executed for every HTTP request
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. Get the "Authorization" header from the incoming request
        final String authorizationHeader = request.getHeader("Authorization");

        String email = null; // will store email extracted from JWT
        String jwt = null; // will store the token itself

        // 2. Check if the header contains a Bearer token
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            email = jwtUtil.extractUsername(jwt);
        }

        // 3. Check if email is extracted and user is not already authenticated
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load user details (from DB) for this email
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // 4. Validate the token with user details
            if (jwtUtil.validateToken(jwt, userDetails)) {

                // 5. Create an authentication token with user details and roles
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // Attach request details to the authentication token
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 6. Set authentication in the security context
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        // 7. Continue processing the request (pass to next filter or controller)
        filterChain.doFilter(request, response);
    }
}
