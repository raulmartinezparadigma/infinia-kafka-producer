package com.infinia.producer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ImageGenerationResponse {

    private boolean success;
    private String imageUrl;

}
