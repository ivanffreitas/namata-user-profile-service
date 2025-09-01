package com.namata.userprofile.client;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class TrailServiceClientConfig {

    @Bean("trailServiceErrorDecoder")
    public ErrorDecoder errorDecoder() {
        return new TrailServiceErrorDecoder();
    }

    @Bean("trailServiceRequestOptions")
    public Request.Options trailServiceRequestOptions() {
        return new Request.Options(5000, TimeUnit.MILLISECONDS, 10000, TimeUnit.MILLISECONDS, true);
    }

    @Bean("trailServiceRetryer")
    public Retryer trailServiceRetryer() {
        return new Retryer.Default(100, 1000, 3);
    }

    @Bean("trailServiceFeignLoggerLevel")
    public Logger.Level trailServiceFeignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    public static class TrailServiceErrorDecoder implements ErrorDecoder {
        private final ErrorDecoder defaultErrorDecoder = new ErrorDecoder.Default();

        @Override
        public Exception decode(String methodKey, feign.Response response) {
            switch (response.status()) {
                case 401:
                    return new RuntimeException("Token inválido ou expirado");
                case 403:
                    return new RuntimeException("Acesso negado");
                case 404:
                    return new RuntimeException("Trilha não encontrada");
                case 500:
                    return new RuntimeException("Erro interno do serviço de trilhas");
                default:
                    return defaultErrorDecoder.decode(methodKey, response);
            }
        }
    }
}