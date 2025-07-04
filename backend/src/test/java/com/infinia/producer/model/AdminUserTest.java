package com.infinia.producer.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminUserTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        AdminUser user = new AdminUser();

        // Act
        user.setId(1L);
        user.setUsername("admin");
        user.setEmail("admin@example.com");
        user.setPassword("password");
        user.setEnabled(true);
        user.setRoles("ROLE_ADMIN");

        // Assert
        assertEquals(1L, user.getId());
        assertEquals("admin", user.getUsername());
        assertEquals("admin@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertTrue(user.isEnabled());
        assertEquals("ROLE_ADMIN", user.getRoles());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        AdminUser user = new AdminUser(2L, "user", "user@example.com", "pass", false, "ROLE_USER");

        // Assert
        assertEquals(2L, user.getId());
        assertEquals("user", user.getUsername());
        assertEquals("user@example.com", user.getEmail());
        assertEquals("pass", user.getPassword());
        assertFalse(user.isEnabled());
        assertEquals("ROLE_USER", user.getRoles());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        AdminUser user1 = new AdminUser(1L, "admin", "admin@example.com", "password", true, "ROLE_ADMIN");
        AdminUser user2 = new AdminUser(1L, "admin", "admin@example.com", "password", true, "ROLE_ADMIN");
        AdminUser user3 = new AdminUser(2L, "user", "user@example.com", "pass", false, "ROLE_USER");

        // Assert
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1, user3);
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        AdminUser user = new AdminUser();
        user.setUsername("testuser");

        // Act & Assert
        assertTrue(user.toString().contains("username=testuser"));
    }
}
