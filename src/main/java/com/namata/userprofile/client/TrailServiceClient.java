package com.namata.userprofile.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;
import java.util.UUID;

@FeignClient(
    name = "trail-service",
    url = "${app.services.trail-service.url:http://192.168.1.9:8085}",
    configuration = TrailServiceClientConfig.class
)
public interface TrailServiceClient {

    @GetMapping("/api/trails/{trailId}")
    Map<String, Object> getTrailById(@PathVariable("trailId") UUID trailId);
}