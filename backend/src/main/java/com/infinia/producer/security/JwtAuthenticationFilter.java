package com.infinia.producer.security;

import com.infinia.producer.service.AdminUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AdminUserDetailsService adminUserDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        log.info("Processing request to: {}", request.getRequestURI());
        log.info("Authorization Header: {}", authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Authorization header is missing or does not start with Bearer. Passing to next filter.");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        log.info("Extracted JWT: {}", jwt);
        try {
            username = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            log.error("Error extracting username from JWT", e);
            filterChain.doFilter(request, response);
            return;
        }
        log.info("Username extracted from JWT: {}", username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = null;

            List<String> rolesInToken = jwtService.extractClaim(jwt, claims -> claims.get("roles", List.class));
            log.info("Roles extracted from JWT: {}", rolesInToken);

            if (rolesInToken != null && rolesInToken.contains("ROLE_ADMIN")) {
                log.info("User has ROLE_ADMIN. Loading user details...");
                userDetails = this.adminUserDetailsService.loadUserByUsername(username);
            } else {
                log.warn("User does not have ROLE_ADMIN or roles claim is missing.");
            }

            if (userDetails != null && jwtService.isTokenValid(jwt, userDetails)) {
                log.info("Token is valid. Setting authentication in SecurityContext.");
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                log.warn("Token is invalid or user details could not be loaded.");
            }
        }

        filterChain.doFilter(request, response);
    }
}