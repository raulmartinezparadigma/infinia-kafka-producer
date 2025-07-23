package com.infinia.producer.service;

import com.infinia.producer.model.User;
import com.infinia.producer.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class DatabaseUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DatabaseUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExistsWithRoles() {
        User user = new User(1L, "testuser", "test@test.com", "password", true, "ROLE_USER,ROLE_ADMIN");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertEquals(2, userDetails.getAuthorities().size());
    }

    @Test
    void loadUserByUsername_shouldAssignDefaultAdminRole_whenUserExistsWithoutRoles() {
        User user = new User(1L, "testuser", "test@test.com", "password", true, null);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN")));
        assertEquals(1, userDetails.getAuthorities().size());
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistentuser");
        });

        String expectedMessage = "User not found with username: nonexistentuser";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }
}
