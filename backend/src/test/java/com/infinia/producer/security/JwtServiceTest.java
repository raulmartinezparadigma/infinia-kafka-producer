package com.infinia.producer.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        // Set a test secret key and expiration. The key must be Base64 encoded and long enough for HS256.
        String testSecret = "bXktc3VwZXItc2VjcmV0LWtleS1mb3ItdGVzdGluZy0xMjM0NQ=="; // A sample Base64 key
        long testExpiration = 3600000; // 1 hour
        ReflectionTestUtils.setField(jwtService, "secretKey", testSecret);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", testExpiration);

        userDetails = new User("testuser", "password", List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);
        assertEquals("testuser", username);
    }

    @Test
    void generateToken_shouldCreateValidToken() {
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenUsernameIsDifferent() {
        String token = jwtService.generateToken(userDetails);
        UserDetails otherUserDetails = new User("otheruser", "password", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        assertFalse(jwtService.isTokenValid(token, otherUserDetails));
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenTokenIsExpired() throws InterruptedException {
        // Use a very short expiration time
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1L); // 1 millisecond
        String token = jwtService.generateToken(userDetails);
        
        // Wait for the token to expire
        Thread.sleep(50);

        assertFalse(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void extractClaim_shouldExtractRolesCorrectly() {
        UserDetails adminUserDetails = new User("admin", "password", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        String token = jwtService.generateToken(adminUserDetails);

        @SuppressWarnings("unchecked")
        List<String> roles = jwtService.extractClaim(token, claims -> claims.get("roles", List.class));

        assertNotNull(roles);
        assertEquals(1, roles.size());
        assertEquals("ROLE_ADMIN", roles.get(0));
    }
}
