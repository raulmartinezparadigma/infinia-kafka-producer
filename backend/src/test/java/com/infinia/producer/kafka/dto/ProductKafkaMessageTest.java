package com.infinia.producer.kafka.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductKafkaMessageTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        ProductKafkaMessage message = new ProductKafkaMessage();

        // Act
        message.setId("id-1");
        message.setSkuId("sku-123");
        message.setType("T-SHIRT");
        message.setDescription("A nice t-shirt");
        message.setPrice(new BigDecimal("19.99"));
        message.setSize("M");
        message.setImageUrl("http://example.com/image.jpg");

        // Assert
        assertEquals("id-1", message.getId());
        assertEquals("sku-123", message.getSkuId());
        assertEquals("T-SHIRT", message.getType());
        assertEquals("A nice t-shirt", message.getDescription());
        assertEquals(new BigDecimal("19.99"), message.getPrice());
        assertEquals("M", message.getSize());
        assertEquals("http://example.com/image.jpg", message.getImageUrl());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        BigDecimal price = new BigDecimal("29.99");
        ProductKafkaMessage message = new ProductKafkaMessage(
                "id-2", "sku-456", "HOODIE", "A warm hoodie",
                price, "L", "http://example.com/hoodie.jpg"
        );

        // Assert
        assertEquals("id-2", message.getId());
        assertEquals("sku-456", message.getSkuId());
        assertEquals("HOODIE", message.getType());
        assertEquals("A warm hoodie", message.getDescription());
        assertEquals(price, message.getPrice());
        assertEquals("L", message.getSize());
        assertEquals("http://example.com/hoodie.jpg", message.getImageUrl());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        BigDecimal price = new BigDecimal("9.99");
        ProductKafkaMessage message1 = new ProductKafkaMessage(
                "id-3", "sku-789", "SOCKS", "A pair of socks",
                price, "S", "http://example.com/socks.jpg"
        );
        ProductKafkaMessage message2 = new ProductKafkaMessage(
                "id-3", "sku-789", "SOCKS", "A pair of socks",
                price, "S", "http://example.com/socks.jpg"
        );

        // Assert
        assertEquals(message1, message2);
        assertEquals(message1.hashCode(), message2.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        ProductKafkaMessage message = new ProductKafkaMessage();
        message.setId("id-1");
        message.setSkuId("sku-123");

        // Act & Assert
        assertTrue(message.toString().contains("id=id-1"));
        assertTrue(message.toString().contains("skuId=sku-123"));
    }
}
