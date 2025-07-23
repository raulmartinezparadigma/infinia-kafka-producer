package com.infinia.producer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinia.producer.dto.ImageGenerationRequest;
import com.infinia.producer.dto.ImageGenerationResponse;
import com.infinia.producer.service.ImageGenerationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ImageGenerationService imageGenerationService;

    @InjectMocks
    private AiController aiController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(aiController).build();
    }

    @Test
    void generateImage_shouldReturnImageResponse_whenSuccessful() throws Exception {
        // Given
        ImageGenerationRequest request = new ImageGenerationRequest();
        request.setDescription("A cat sitting on a mat");

        ImageGenerationResponse serviceResponse = new ImageGenerationResponse(true, "http://localhost:8081/generated-images/a_cat_sitting_on_a_mat.png");
        when(imageGenerationService.generateImage(anyString())).thenReturn(serviceResponse);

        // When & Then
        mockMvc.perform(post("/api/ai/generate-image")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.imageUrl").value("http://localhost:8081/generated-images/a_cat_sitting_on_a_mat.png"));
    }
}
