package com.infinia.producer;

import com.infinia.producer.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class InfiniaKafkaProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(InfiniaKafkaProducerApplication.class, args);
	}

}
