package com.infinia.producer.service;

import com.infinia.producer.model.AdminUser;
import com.infinia.producer.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service("adminUserDetailsService")
@RequiredArgsConstructor
public class AdminUserDetailsService implements UserDetailsService {

    private final AdminUserRepository adminUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("[TRACE] Buscando admin: " + username);
        AdminUser admin = adminUserRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println("[TRACE] Admin no encontrado: " + username);
                    return new UsernameNotFoundException("Admin no encontrado: " + username);
                });
        System.out.println("[TRACE] Admin encontrado: " + admin.getUsername());
        System.out.println("[TRACE] Password recuperado: " + admin.getPassword());
        System.out.println("[TRACE] Habilitado: " + admin.isEnabled());
        return User
                .withUsername(admin.getUsername())
                .password(admin.getPassword())
                .roles("ADMIN")
                .disabled(!admin.isEnabled())
                .build();
    }
}
