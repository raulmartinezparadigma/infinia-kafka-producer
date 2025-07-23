package com.infinia.producer.service;

import com.google.gson.Gson;
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
import java.text.Normalizer;
import java.util.Base64;
import java.util.regex.Pattern;

@Service
public class ImageGenerationService {

    private static final Logger logger = LoggerFactory.getLogger(ImageGenerationService.class);
    private static final String CLOUDFLARE_API_URL_TEMPLATE = "https://api.cloudflare.com/client/v4/accounts/%s/ai/run/@cf/black-forest-labs/flux-1-schnell";

    @Value("${cloudflare.account.id}")
    private String accountId;

    @Value("${cloudflare.api.token}")
    private String apiToken;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final Gson gson = new Gson();

    public ImageGenerationResponse generateImage(String description) {
        String finalPrompt = buildPrompt(description);
        String apiUrl = String.format(CLOUDFLARE_API_URL_TEMPLATE, accountId);

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("prompt", finalPrompt);

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(jsonBody.toString(), mediaType);

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .addHeader("Authorization", "Bearer " + apiToken)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                logger.error("Error en la API de Cloudflare: {} - {}", response.code(), errorBody);
                return new ImageGenerationResponse(false, null);
            }

            String responseBody = response.body().string();
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            String base64Image = jsonResponse.getAsJsonObject("result").get("image").getAsString();

            // Decodificar y guardar la imagen
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            String fileName = generateSlug(description) + ".png";

            String projectRoot = new File(".").getCanonicalPath();
            Path imageFolderPath = Paths.get(projectRoot, "..", "infinia-images");

            if (!Files.exists(imageFolderPath)) {
                Files.createDirectories(imageFolderPath);
            }

            Path imagePath = imageFolderPath.resolve(fileName);
            Files.write(imagePath, imageBytes);

            String imageUrl = "/generated-images/" + fileName;
            logger.info("Imagen generada y guardada en: {}", imageUrl);
            return new ImageGenerationResponse(true, imageUrl);

        } catch (IOException e) {
            logger.error("Error de IO al llamar a la API de Cloudflare o al guardar la imagen", e);
            return new ImageGenerationResponse(false, null);
        }
    }

    private String buildPrompt(String baseDescription) {
        String styleEnhancers = ", product photography, professional, high quality, 8k, photorealistic, studio lighting, minimalist background";
        return baseDescription + styleEnhancers;
    }

    private String generateSlug(String input) {
        // Normalizar para quitar acentos
        String noAccents = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        noAccents = pattern.matcher(noAccents).replaceAll("");

        // Reemplazar espacios y caracteres no alfanuméricos por guiones bajos
        return noAccents.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "_");
    }
}
