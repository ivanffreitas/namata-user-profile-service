package com.namata.userprofile.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Value("${app.upload.profile-pictures.directory:/var/namata/uploads/profile-pictures}")
    private String profilePicturesDirectory;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configurar para servir imagens de perfil
        registry.addResourceHandler("/uploads/profile-pictures/**")
                .addResourceLocations("file:" + profilePicturesDirectory + "/");
    }
}