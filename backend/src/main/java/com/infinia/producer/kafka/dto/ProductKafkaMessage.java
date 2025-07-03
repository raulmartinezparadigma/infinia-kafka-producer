package com.infinia.producer.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for product messages sent via Kafka.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductKafkaMessage {
    private String id;
    /**
     * Unique business identifier (SKU), 18 digits without hyphens
     */
    private String skuId;
    private String type;
    private String description;
    private BigDecimal price;
    private String size;
    private String imageUrl;
}
