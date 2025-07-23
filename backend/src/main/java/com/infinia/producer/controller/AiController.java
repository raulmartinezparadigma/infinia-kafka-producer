package com.infinia.producer.controller;

import com.infinia.producer.dto.ImageGenerationRequest;
import com.infinia.producer.dto.ImageGenerationResponse;
import com.infinia.producer.service.ImageGenerationService;
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
    public ResponseEntity<ImageGenerationResponse> generateImage(@RequestBody ImageGenerationRequest request) {
        ImageGenerationResponse response = imageGenerationService.generateImage(request.getDescription());
        return ResponseEntity.ok(response);
    }
}
