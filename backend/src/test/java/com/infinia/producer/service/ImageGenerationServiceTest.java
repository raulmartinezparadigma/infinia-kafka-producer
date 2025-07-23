package com.infinia.producer.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.infinia.producer.dto.ImageGenerationResponse;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ImageGenerationServiceTest {

    @InjectMocks
    private ImageGenerationService imageGenerationService;

    @Mock
    private OkHttpClient httpClient;

    @Mock
    private Call call;

    @Mock
    private Response response;

    @Mock
    private ResponseBody responseBody;

    private final Gson gson = new Gson();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(imageGenerationService, "accountId", "test-account-id");
        ReflectionTestUtils.setField(imageGenerationService, "apiToken", "test-api-token");
        ReflectionTestUtils.setField(imageGenerationService, "httpClient", httpClient);
    }

    @Test
    void generateImage_shouldReturnSuccessWithImageUrl_whenApiCallIsSuccessful() throws IOException {
        // Arrange
        String description = "a cat";
        String base64Image = "dGVzdC1pbWFnZQ=="; // "test-image" en Base64

        JsonObject result = new JsonObject();
        result.addProperty("image", base64Image);
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("result", result);

        when(httpClient.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(true);
        when(response.body()).thenReturn(responseBody);
        when(responseBody.string()).thenReturn(gson.toJson(jsonResponse));

        // Act
        ImageGenerationResponse resultResponse = imageGenerationService.generateImage(description);

        // Assert
        assertTrue(resultResponse.isSuccess());
        assertEquals("/generated-images/a_cat.png", resultResponse.getImageUrl());
    }

    @Test
    void generateImage_shouldReturnFailure_whenApiCallFails() throws IOException {
        // Arrange
        String description = "a dog";

        when(httpClient.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(false);
        when(response.body()).thenReturn(responseBody);
        when(responseBody.string()).thenReturn("Error from API");

        // Act
        ImageGenerationResponse resultResponse = imageGenerationService.generateImage(description);

        // Assert
        assertFalse(resultResponse.isSuccess());
        assertNull(resultResponse.getImageUrl());
    }

    @Test
    void generateImage_shouldReturnFailure_whenIOExceptionOccurs() throws IOException {
        // Arrange
        String description = "a bird";

        when(httpClient.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenThrow(new IOException("Network error"));

        // Act
        ImageGenerationResponse resultResponse = imageGenerationService.generateImage(description);

        // Assert
        assertFalse(resultResponse.isSuccess());
        assertNull(resultResponse.getImageUrl());
    }
}
