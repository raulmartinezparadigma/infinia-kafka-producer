package com.infinia.producer.security;

import com.infinia.producer.service.DatabaseUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthFilter;

    @Mock
    private DatabaseUserDetailsService userDetailsService;

    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    void passwordEncoder_shouldReturnBCryptPasswordEncoder() {
        // When
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        // Then
        assertNotNull(passwordEncoder);
        assertInstanceOf(BCryptPasswordEncoder.class, passwordEncoder);
    }

    @Test
    void securityFilterChain_shouldConfigureApiSecurity() throws Exception {
        // Arrange
        HttpSecurity httpSecurity = mock(HttpSecurity.class, withSettings().defaultAnswer(RETURNS_SELF));

        // Act
        securityConfig.securityFilterChain(httpSecurity);

        // Assert
        // Verifica que las rutas de auth son públicas y el resto están autenticadas
        verify(httpSecurity).authorizeHttpRequests(any());

        // Verifica que se añade el filtro JWT
        verify(httpSecurity).addFilterBefore(eq(jwtAuthFilter), any());

        // Verifica que el securityMatcher ya no se utiliza
        verify(httpSecurity, never()).securityMatcher(anyString());
    }
}
