package com.infinia.producer.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtPropertiesTest {

    @Test
    void jwtProperties_shouldHoldDataCorrectly() {
        // Arrange
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("my-secret");
        jwtProperties.setExpiration(1000L);

        // Assert
        assertEquals("my-secret", jwtProperties.getSecret());
        assertEquals(1000L, jwtProperties.getExpiration());
    }
}
