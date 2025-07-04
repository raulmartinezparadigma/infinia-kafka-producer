package com.infinia.producer.repository;

import com.infinia.producer.model.AdminUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AdminUserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Test
    void findByUsername_shouldReturnUser_whenUserExists() {
        // Arrange
        AdminUser adminUser = new AdminUser();
        adminUser.setUsername("testadmin");
        adminUser.setPassword("password");
        adminUser.setEnabled(true);
        entityManager.persistAndFlush(adminUser);

        // Act
        Optional<AdminUser> foundUser = adminUserRepository.findByUsername("testadmin");

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals("testadmin", foundUser.get().getUsername());
    }

    @Test
    void findByUsername_shouldReturnEmpty_whenUserDoesNotExist() {
        // Act
        Optional<AdminUser> foundUser = adminUserRepository.findByUsername("nonexistent");

        // Assert
        assertFalse(foundUser.isPresent());
    }
}
