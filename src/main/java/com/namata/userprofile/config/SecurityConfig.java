package com.namata.userprofile.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.auth.jwt.secret}")
    private String jwtSecret;

    @Bean
    @Order(1)
    public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/api/v1/test/**")
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz.anyRequest().permitAll())
                .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // Endpoints públicos (sem autenticação)
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/v1/badges/active", "/api/v1/badges/type/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/statistics/ranking/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/statistics/averages/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/statistics/max/**").permitAll()
                        // Endpoints de ranking e busca pública de perfis
                        .requestMatchers(HttpMethod.GET, "/api/v1/profiles/ranking/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/profiles/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/profiles/location/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/profiles/experience/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/profiles/active").permitAll()

                        // Endpoints que requerem autenticação
                        .requestMatchers("/api/v1/profiles/**").authenticated()
                        .requestMatchers("/api/v1/activities/**").authenticated()
                        .requestMatchers("/api/v1/achievements/**").authenticated()
                        .requestMatchers("/api/v1/statistics/user/**").authenticated()

                        // Endpoints administrativos
                        .requestMatchers(HttpMethod.POST, "/api/v1/badges/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/badges/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/badges/**").hasRole("ADMIN")

                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Usar o mesmo método que o auth-service para garantir compatibilidade
        byte[] keyBytes = jwtSecret.getBytes();
        return NimbusJwtDecoder.withSecretKey(new javax.crypto.spec.SecretKeySpec(keyBytes, "HmacSHA256"))
                .macAlgorithm(org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("");
        authoritiesConverter.setAuthoritiesClaimName("roles");

        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        authenticationConverter.setPrincipalClaimName("sub");

        return authenticationConverter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}