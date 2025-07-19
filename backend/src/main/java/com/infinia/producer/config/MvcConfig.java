package com.infinia.producer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        try {
            // Obtenemos la ruta a la carpeta 'infinia-images' de la misma forma que en el servicio
            String projectRoot = new File(".").getCanonicalPath();
            Path imageFolderPath = Paths.get(projectRoot, "..", "infinia-images");
            String imageFolderUri = imageFolderPath.toUri().toString();

            // Mapeamos la ruta URL a la ruta física del directorio
            registry.addResourceHandler("/generated-images/**")
                    .addResourceLocations(imageFolderUri);

        } catch (IOException e) {
            throw new RuntimeException("No se pudo configurar el manejador de recursos para las imágenes.", e);
        }
    }
}
