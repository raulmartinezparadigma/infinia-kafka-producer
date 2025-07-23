package com.infinia.producer.security;

import com.infinia.producer.service.DatabaseUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private DatabaseUserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(request.getServletPath()).thenReturn("/api/some-path");

        userDetails = User.withUsername("testuser")
                .password("password")
                .authorities("ROLE_USER").build();
    }

    @Test
    void doFilterInternal_shouldPass_whenAuthHeaderIsMissing() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(securityContext, never()).setAuthentication(any());
    }

    @Test
    void doFilterInternal_shouldSetAuthentication_whenTokenIsValid() throws ServletException, IOException {
        // Given
        String token = "valid-token";
        String username = "testuser";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(null);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(securityContext).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldNotSetAuthentication_whenTokenIsInvalid() throws ServletException, IOException {
        // Given
        String token = "invalid-token";
        String username = "testuser";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(null);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(false);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }
}
