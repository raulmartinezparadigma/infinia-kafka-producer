package com.infinia.producer.security;

import com.infinia.producer.service.AdminUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthFilter;

    @Mock
    private AdminUserDetailsService adminUserDetailsService;

    @InjectMocks
    @Spy
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
    @SuppressWarnings("unchecked") // Suppress warnings for generic types in mock/captor
    void securityFilterChain_shouldConfigureHttpSecurityCorrectly() throws Exception {
        // Arrange
        // Use RETURNS_SELF to correctly mock the fluent API of HttpSecurity
        HttpSecurity httpSecurity = mock(HttpSecurity.class, withSettings().defaultAnswer(RETURNS_SELF));

        // When
        securityConfig.securityFilterChain(httpSecurity);

        // Then
        // Verify CSRF and CORS are configured
        verify(httpSecurity).csrf(any(Customizer.class));
        verify(httpSecurity).cors(any(Customizer.class));

        // Verify request authorization rules by capturing the lambda
        ArgumentCaptor<Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>> authCaptor = ArgumentCaptor.forClass(Customizer.class);
        verify(httpSecurity).authorizeHttpRequests(authCaptor.capture());

        // --- Mocking the fluent API chain correctly ---
        // 1. Create a mock for the registry
        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry = mock(AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry.class);

        // 2. Create mocks for the intermediate objects returned by requestMatchers() and anyRequest()
        var authMatcher = mock(AuthorizeHttpRequestsConfigurer.AuthorizedUrl.class);
        var adminMatcher = mock(AuthorizeHttpRequestsConfigurer.AuthorizedUrl.class);
        var anyRequestMatcher = mock(AuthorizeHttpRequestsConfigurer.AuthorizedUrl.class);

        // 3. Configure the registry to return the intermediate mocks
        when(registry.requestMatchers("/api/auth/**")).thenReturn(authMatcher);
        when(registry.requestMatchers("/api/admin/**")).thenReturn(adminMatcher);
        when(registry.anyRequest()).thenReturn(anyRequestMatcher);

        // 4. CRUCIAL: Configure the intermediate mocks to return the registry to allow chaining
        when(authMatcher.permitAll()).thenReturn(registry);
        when(adminMatcher.hasAuthority("ROLE_ADMIN")).thenReturn(registry);
        when(anyRequestMatcher.authenticated()).thenReturn(registry);

        // 5. Execute the captured lambda with our fully configured mock
        authCaptor.getValue().customize(registry);

        // 6. Verify that the correct methods were called in order
        var inOrder = inOrder(registry, authMatcher, adminMatcher, anyRequestMatcher);
        inOrder.verify(registry).requestMatchers("/api/auth/**");
        inOrder.verify(authMatcher).permitAll();
        inOrder.verify(registry).requestMatchers("/api/admin/**");
        inOrder.verify(adminMatcher).hasAuthority("ROLE_ADMIN");
        inOrder.verify(registry).anyRequest();
        inOrder.verify(anyRequestMatcher).authenticated();

        // Verify session management policy
        ArgumentCaptor<Customizer<org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer<HttpSecurity>>> sessionCaptor = ArgumentCaptor.forClass(Customizer.class);
        verify(httpSecurity).sessionManagement(sessionCaptor.capture());
        var sessionConfigurer = mock(org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer.class);
        sessionCaptor.getValue().customize(sessionConfigurer);
        verify(sessionConfigurer).sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Verify authentication provider is set
        verify(httpSecurity).authenticationProvider(any(AuthenticationProvider.class));

        // Verify JWT filter is added before the standard auth filter
        verify(httpSecurity).addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // Verify the chain is built
        verify(httpSecurity).build();
    }
}
