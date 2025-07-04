package com.infinia.producer.security;

import com.infinia.producer.service.AdminUserDetailsService;
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
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private AdminUserDetailsService adminUserDetailsService;

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

    private UserDetails adminUserDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);

        adminUserDetails = User.withUsername("admin")
                .password("password")
                .authorities("ROLE_ADMIN").build();
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
    void doFilterInternal_shouldPass_whenAuthHeaderIsInvalid() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Invalid Header");

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(securityContext, never()).setAuthentication(any());
    }

    @Test
    void doFilterInternal_shouldSetAuthentication_whenTokenIsValidAdmin() throws ServletException, IOException {
        // Given
        String token = "valid-admin-token";
        String username = "admin";
        List<String> roles = Collections.singletonList("ROLE_ADMIN");

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(null);
        when(jwtService.extractClaim(eq(token), any())).thenReturn(roles);
        when(adminUserDetailsService.loadUserByUsername(username)).thenReturn(adminUserDetails);
        when(jwtService.isTokenValid(token, adminUserDetails)).thenReturn(true);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(securityContext).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldNotSetAuthentication_whenTokenIsForNonAdmin() throws ServletException, IOException {
        // Given
        String token = "valid-user-token";
        String username = "user";
        List<String> roles = Collections.singletonList("ROLE_USER");

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(null);
        when(jwtService.extractClaim(eq(token), any())).thenReturn(roles);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(adminUserDetailsService, never()).loadUserByUsername(anyString());
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldNotSetAuthentication_whenTokenIsInvalid() throws ServletException, IOException {
        // Given
        String token = "invalid-token";
        String username = "admin";
        List<String> roles = Collections.singletonList("ROLE_ADMIN");

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(null);
        when(jwtService.extractClaim(eq(token), any())).thenReturn(roles);
        when(adminUserDetailsService.loadUserByUsername(username)).thenReturn(adminUserDetails);
        when(jwtService.isTokenValid(token, adminUserDetails)).thenReturn(false);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldPass_whenUsernameExtractionFails() throws ServletException, IOException {
        // Given
        String token = "bad-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenThrow(new RuntimeException("JWT parsing error"));

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }
}
