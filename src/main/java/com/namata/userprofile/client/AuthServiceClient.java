package com.namata.userprofile.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;
import java.util.UUID;

@FeignClient(
    name = "auth-service",
    url = "${app.services.auth-service.url:http://localhost:8080}",
    configuration = AuthServiceClientConfig.class
)
public interface AuthServiceClient {

    @GetMapping("/api/v1/auth/validate")
    Map<String, Object> validateToken(@RequestHeader("Authorization") String token);

    @GetMapping("/api/v1/users/{userId}")
    Map<String, Object> getUserById(@PathVariable("userId") UUID userId);

    @GetMapping("/api/v1/users/{userId}/exists")
    Boolean userExists(
            @PathVariable("userId") UUID userId,
            @RequestHeader("Authorization") String token);
}