package com.infinia.producer.service;

import com.infinia.producer.model.AdminUser;
import com.infinia.producer.repository.AdminUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserDetailsServiceTest {

    @Mock
    private AdminUserRepository adminUserRepository;

    @InjectMocks
    private AdminUserDetailsService adminUserDetailsService;

    private AdminUser adminUser;

    @BeforeEach
    void setUp() {
        adminUser = new AdminUser();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setPassword("encodedPassword");
        adminUser.setEnabled(true);
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        when(adminUserRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

        UserDetails userDetails = adminUserDetailsService.loadUserByUsername("admin");

        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsername_shouldThrowUsernameNotFoundException_whenUserDoesNotExist() {
        when(adminUserRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            adminUserDetailsService.loadUserByUsername("unknown");
        });
    }
    
    @Test
    void loadUserByUsername_shouldReturnDisabledUserDetails_whenUserIsDisabled() {
        adminUser.setEnabled(false);
        when(adminUserRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

        UserDetails userDetails = adminUserDetailsService.loadUserByUsername("admin");

        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
        assertFalse(userDetails.isEnabled());
    }
}
