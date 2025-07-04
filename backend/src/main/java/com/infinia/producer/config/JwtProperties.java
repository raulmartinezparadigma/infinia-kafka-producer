package com.infinia.producer.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
public class JwtProperties {

    /**
     * Clave secreta para firmar los tokens JWT
     */
    private String secret;

    /**
     * Tiempo de expiración del token en milisegundos
     */
    private long expiration;
}
