package com.infinia.producer.kafka;

import com.infinia.producer.kafka.dto.ProductKafkaMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductProducerTest {

    @Mock
    private KafkaTemplate<String, ProductKafkaMessage> kafkaTemplate;

    @InjectMocks
    private ProductProducer productProducer;

    @Test
    void sendProduct_shouldCallKafkaTemplateSend() {
        // Arrange
        ProductKafkaMessage message = new ProductKafkaMessage();
        message.setId("test-id");
        message.setSkuId("sku-123");

        String expectedTopic = "products-topic";

        // Act
        productProducer.sendProduct(message);

        // Assert
        verify(kafkaTemplate).send(expectedTopic, message.getId(), message);
    }
}
