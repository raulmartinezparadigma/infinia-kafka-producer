package com.infinia.producer.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminAuthRequestDTOTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        AdminAuthRequestDTO dto = new AdminAuthRequestDTO();

        // Act
        dto.setUsername("admin");
        dto.setPassword("password123");

        // Assert
        assertEquals("admin", dto.getUsername());
        assertEquals("password123", dto.getPassword());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        AdminAuthRequestDTO dto1 = new AdminAuthRequestDTO();
        dto1.setUsername("user");
        dto1.setPassword("pass");

        AdminAuthRequestDTO dto2 = new AdminAuthRequestDTO();
        dto2.setUsername("user");
        dto2.setPassword("pass");

        AdminAuthRequestDTO dto3 = new AdminAuthRequestDTO();
        dto3.setUsername("anotherUser");
        dto3.setPassword("pass");

        // Assert
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        AdminAuthRequestDTO dto = new AdminAuthRequestDTO();
        dto.setUsername("test");

        // Act & Assert
        assertTrue(dto.toString().contains("username=test"));
    }
}
