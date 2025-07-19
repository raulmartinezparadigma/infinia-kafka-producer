package com.infinia.producer.controller;

import com.infinia.producer.dto.ImageGenerationRequest;
import com.infinia.producer.dto.ImageGenerationResponse;
import com.infinia.producer.service.ImageGenerationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final ImageGenerationService imageGenerationService;

    public AiController(ImageGenerationService imageGenerationService) {
        this.imageGenerationService = imageGenerationService;
    }

    @PostMapping("/generate-image")
    public ResponseEntity<ImageGenerationResponse> generateImage(@RequestBody ImageGenerationRequest request, HttpServletRequest servletRequest) {
        ImageGenerationResponse response = imageGenerationService.generateImage(request.getDescription());

        // Construir la URL base del servidor dinámicamente
        String baseUrl = servletRequest.getScheme() + "://" + servletRequest.getServerName() + ":" + servletRequest.getServerPort();
        // CORRECCIÓN: response.getImageUrl() ya incluye la barra inicial, no necesitamos añadir otra.
        String fullImageUrl = baseUrl + response.getImageUrl();

        // Actualizar el objeto de respuesta con la URL completa
        response.setImageUrl(fullImageUrl);

        return ResponseEntity.ok(response);
    }
}
