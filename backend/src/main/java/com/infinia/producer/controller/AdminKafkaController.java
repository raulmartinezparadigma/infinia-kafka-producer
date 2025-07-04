package com.infinia.producer.controller;

import com.infinia.producer.kafka.ProductProducer;
import com.infinia.producer.kafka.dto.ProductKafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller to handle product submissions to the Kafka topic.
 */
@RestController
@RequestMapping("/api/admin/kafka")
@RequiredArgsConstructor
@Slf4j
public class AdminKafkaController {

    private final ProductProducer productProducer;

    /**
     * Receives a product in JSON format and sends it to the Kafka topic.
     * This endpoint is protected and requires ADMIN role.
     */
    @PostMapping("/product")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> sendProductToKafka(@RequestBody ProductKafkaMessage productKafkaMessage) {
        try {
            if (productKafkaMessage.getSkuId() == null || productKafkaMessage.getSkuId().isBlank()) {
                return ResponseEntity.badRequest().body("The skuId field is mandatory");
            }
            productProducer.sendProduct(productKafkaMessage);
            return ResponseEntity.ok().body("Product sent successfully to Kafka");
        } catch (Exception e) {
            log.error("Error sending product to Kafka", e);
            return ResponseEntity.internalServerError().body("Error sending product to Kafka: " + e.getMessage());
        }
    }
}
