package com.appanime.appanime.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String avatarDirectory = "file:./images/avatar/"; // Ruta donde están las imágenes de avatar de cada usuario

        registry.addResourceHandler("/avatar/**")
                .addResourceLocations(avatarDirectory);
    }
}