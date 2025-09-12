package com.namata.userprofile.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configurar para servir imagens de perfil
        registry.addResourceHandler("/uploads/profile-pictures/**")
                .addResourceLocations("file:/var/namata/uploads/profile-pictures/");
    }
}