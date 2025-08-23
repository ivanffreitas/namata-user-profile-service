package com.namata.userprofile.client;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class AuthServiceClientConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(
                5000, TimeUnit.MILLISECONDS,  // connectTimeout
                10000, TimeUnit.MILLISECONDS, // readTimeout
                true                          // followRedirects
        );
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(
                100,   // period
                1000,  // maxPeriod
                3      // maxAttempts
        );
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new AuthServiceErrorDecoder();
    }

    public static class AuthServiceErrorDecoder implements ErrorDecoder {
        private final ErrorDecoder defaultErrorDecoder = new ErrorDecoder.Default();

        @Override
        public Exception decode(String methodKey, feign.Response response) {
            switch (response.status()) {
                case 401:
                    return new RuntimeException("Token inválido ou expirado");
                case 403:
                    return new RuntimeException("Acesso negado");
                case 404:
                    return new RuntimeException("Usuário não encontrado");
                case 500:
                    return new RuntimeException("Erro interno do serviço de autenticação");
                default:
                    return defaultErrorDecoder.decode(methodKey, response);
            }
        }
    }
}