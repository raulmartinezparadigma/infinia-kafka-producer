package com.infinia.producer.kafka;

import com.infinia.producer.kafka.dto.ProductKafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Producer service to send product messages to Kafka.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductProducer {

    private static final String PRODUCT_TOPIC = "products-topic";

    private final KafkaTemplate<String, ProductKafkaMessage> kafkaTemplate;

    /**
     * Sends a product message to the Kafka topic.
     * @param productMessage DTO of the product to send.
     */
    public void sendProduct(ProductKafkaMessage productMessage) {
        log.info("Sending product to Kafka: {}", productMessage);
        kafkaTemplate.send(PRODUCT_TOPIC, productMessage.getId(), productMessage);
    }
}
