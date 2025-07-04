package com.infinia.producer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinia.producer.kafka.ProductProducer;
import com.infinia.producer.kafka.dto.ProductKafkaMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminKafkaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductProducer productProducer;

    @InjectMocks
    private AdminKafkaController adminKafkaController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(adminKafkaController).build();
    }

    @Test
    void sendProductToKafka_shouldReturnOk_whenMessageIsValid() throws Exception {
        // Arrange
        ProductKafkaMessage message = new ProductKafkaMessage();
        message.setSkuId("12345");
        doNothing().when(productProducer).sendProduct(any(ProductKafkaMessage.class));

        // Act & Assert
        mockMvc.perform(post("/api/admin/kafka/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(message)))
                .andExpect(status().isOk());

        // Verify
        verify(productProducer).sendProduct(message);
    }

    @Test
    void sendProductToKafka_shouldReturnBadRequest_whenSkuIdIsNull() throws Exception {
        // Arrange
        ProductKafkaMessage message = new ProductKafkaMessage(); // skuId is null

        // Act & Assert
        mockMvc.perform(post("/api/admin/kafka/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(message)))
                .andExpect(status().isBadRequest());

        // Verify
        verify(productProducer, never()).sendProduct(any(ProductKafkaMessage.class));
    }

    @Test
    void sendProductToKafka_shouldReturnBadRequest_whenSkuIdIsEmpty() throws Exception {
        // Arrange
        ProductKafkaMessage message = new ProductKafkaMessage();
        message.setSkuId(""); // skuId is empty

        // Act & Assert
        mockMvc.perform(post("/api/admin/kafka/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(message)))
                .andExpect(status().isBadRequest());

        // Verify
        verify(productProducer, never()).sendProduct(any(ProductKafkaMessage.class));
    }
}
