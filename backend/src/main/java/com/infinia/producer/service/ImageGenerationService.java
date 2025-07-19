package com.infinia.producer.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.infinia.producer.dto.ImageGenerationResponse;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Service
public class ImageGenerationService {

    private static final Logger logger = LoggerFactory.getLogger(ImageGenerationService.class);
    @Value("${stability.api.key}")
    private String apiKey;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final Gson gson = new Gson();

    public ImageGenerationResponse generateImage(String description) {
        String finalPrompt = buildPrompt(description);

        MediaType mediaType = MediaType.parse("application/json");
        String jsonBody = "{\"text_prompts\":[{\"text\":\"" + finalPrompt + "\"}],\"cfg_scale\":7,\"height\":1024,\"width\":1024,\"samples\":1,\"steps\":30}";
        RequestBody body = RequestBody.create(jsonBody, mediaType);

        Request request = new Request.Builder()
                .url("https://api.stability.ai/v1/generation/stable-diffusion-v1-6/text-to-image")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("Error en la API de Stability AI: " + response.body().string());
                return new ImageGenerationResponse(false, null);
            }

            String responseBody = response.body().string();
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            JsonArray artifacts = jsonResponse.getAsJsonArray("artifacts");

            if (artifacts.size() == 0) {
                return new ImageGenerationResponse(false, null);
            }

            String base64Image = artifacts.get(0).getAsJsonObject().get("base64").getAsString();
            String imageUrl = saveImageAndGetUrl(base64Image);

            return new ImageGenerationResponse(true, imageUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return new ImageGenerationResponse(false, null);
        }
    }

    private String buildPrompt(String baseDescription) {
        String styleEnhancers = ", product photography, professional, high quality, 8k, photorealistic, studio lighting, minimalist background";
        return baseDescription + styleEnhancers;
    }

    private String saveImageAndGetUrl(String base64Image) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            String fileName = UUID.randomUUID().toString() + ".png";

            // Paso 1: Obtener la ruta del directorio del proyecto y crear la carpeta externa
            String projectRoot = new File(".").getCanonicalPath();
            Path imageFolderPath = Paths.get(projectRoot, "..", "infinia-images");

            if (!Files.exists(imageFolderPath)) {
                Files.createDirectories(imageFolderPath);
                logger.info("Directorio de imágenes creado en: {}", imageFolderPath.toAbsolutePath());
            }

            // Paso 2: Guardar la imagen en la nueva carpeta
            Path imagePath = imageFolderPath.resolve(fileName);
            Files.write(imagePath, imageBytes);
            logger.info("Imagen guardada en: {}", imagePath.toAbsolutePath());

            // Paso 3: Devolver la URL relativa que usaremos para acceder a la imagen
            return "/generated-images/" + fileName;
        } catch (IOException e) {
            logger.error("Error al guardar la imagen", e);
            throw new RuntimeException("No se pudo guardar la imagen generada.", e);
        }
    }
}
