package com.namata.userprofile.controller;

import com.namata.userprofile.dto.CreateUserProfileRequest;
import com.namata.userprofile.dto.UpdateUserProfileRequest;
import com.namata.userprofile.dto.UserProfileDTO;
import com.namata.userprofile.entity.UserProfile;
import com.namata.userprofile.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Profile", description = "APIs para gerenciamento de perfis de usuário")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PostMapping
    @Operation(summary = "Criar perfil de usuário", description = "Cria um novo perfil de usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Perfil criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "409", description = "Perfil já existe para este usuário")
    })
    public ResponseEntity<UserProfileDTO> createProfile(
            @Valid @RequestBody CreateUserProfileRequest request) {
        log.info("Recebida solicitação para criar perfil para usuário ID: {}", request.getUserId());
        
        UserProfileDTO profile = userProfileService.createProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(profile);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Buscar perfil por ID do usuário", description = "Retorna o perfil de um usuário específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil encontrado"),
        @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    })
    public ResponseEntity<UserProfileDTO> getUserProfile(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId) {
        log.info("Buscando perfil do usuário ID: {}", userId);
        
        Optional<UserProfileDTO> profile = userProfileService.getProfileByUserId(userId);
        return profile.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/ranking/points")
    @Operation(summary = "Ranking por pontos", description = "Retorna o ranking de usuários ordenado por pontos totais")
    @ApiResponse(responseCode = "200", description = "Ranking por pontos")
    public ResponseEntity<Page<UserProfileDTO>> getRankingByPoints(Pageable pageable) {
        log.info("Buscando ranking por pontos");
        
        Page<UserProfileDTO> ranking = userProfileService.getRankingByPoints(pageable);
        return ResponseEntity.ok(ranking);
    }

    @GetMapping("/ranking/trails")
    @Operation(summary = "Ranking por trilhas", description = "Retorna o ranking de usuários ordenado por trilhas completadas")
    @ApiResponse(responseCode = "200", description = "Ranking por trilhas")
    public ResponseEntity<Page<UserProfileDTO>> getRankingByTrails(Pageable pageable) {
        log.info("Buscando ranking por trilhas");
        
        Page<UserProfileDTO> ranking = userProfileService.getRankingByTrails(pageable);
        return ResponseEntity.ok(ranking);
    }

    @GetMapping("/by-id/{profileId}")
    @Operation(summary = "Buscar perfil por ID do perfil", description = "Retorna um perfil específico pelo seu ID de perfil")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil encontrado"),
        @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    })
    public ResponseEntity<UserProfileDTO> getProfileByProfileId(
            @Parameter(description = "ID do perfil") @PathVariable UUID profileId) {
        log.info("Buscando perfil por ID do perfil: {}", profileId);
        
        return userProfileService.getProfileById(profileId)
                .map(profile -> ResponseEntity.ok(profile))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{profileId}")
    @Operation(summary = "Buscar perfil por ID", description = "Retorna um perfil específico pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil encontrado"),
        @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    })
    public ResponseEntity<UserProfileDTO> getProfileById(
            @Parameter(description = "ID do perfil") @PathVariable UUID profileId) {
        log.info("Buscando perfil ID: {}", profileId);
        
        return userProfileService.getProfileById(profileId)
                .map(profile -> ResponseEntity.ok(profile))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/user/{userId}")
    @Operation(summary = "Atualizar perfil", description = "Atualiza os dados de um perfil de usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    })
    public ResponseEntity<UserProfileDTO> updateProfile(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserProfileRequest request) {
        log.info("Atualizando perfil para usuário ID: {}", userId);
        
        try {
            UserProfileDTO updatedProfile = userProfileService.updateProfile(userId, request);
            return ResponseEntity.ok(updatedProfile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/user/{userId}")
    @Operation(summary = "Desativar perfil", description = "Desativa um perfil de usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Perfil desativado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    })
    public ResponseEntity<Void> deactivateProfile(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId) {
        log.info("Desativando perfil para usuário ID: {}", userId);
        
        try {
            userProfileService.deactivateProfile(userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/user/{userId}/verify")
    @Operation(summary = "Verificar perfil", description = "Marca um perfil como verificado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Perfil verificado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    })
    public ResponseEntity<Void> verifyProfile(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId) {
        log.info("Verificando perfil para usuário ID: {}", userId);
        
        try {
            userProfileService.verifyProfile(userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/active")
    @Operation(summary = "Listar perfis ativos", description = "Retorna todos os perfis ativos")
    @ApiResponse(responseCode = "200", description = "Lista de perfis ativos")
    public ResponseEntity<List<UserProfileDTO>> getActiveProfiles() {
        log.info("Buscando perfis ativos");
        
        List<UserProfileDTO> profiles = userProfileService.getActiveProfiles();
        return ResponseEntity.ok(profiles);
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        try {
            long count = userProfileService.countActiveProfiles();
            return ResponseEntity.ok("Active profiles count: " + count);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Pesquisar perfis", description = "Pesquisa perfis com filtros")
    @ApiResponse(responseCode = "200", description = "Resultados da pesquisa")
    public ResponseEntity<Page<UserProfileDTO>> searchProfiles(
            @Parameter(description = "Nome de exibição") @RequestParam(required = false) String displayName,
            @Parameter(description = "Localização") @RequestParam(required = false) String location,
            @Parameter(description = "Nível de experiência") @RequestParam(required = false) UserProfile.ExperienceLevel experienceLevel,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Pesquisando perfis com filtros - displayName: {}, location: {}, experienceLevel: {}", 
                displayName, location, experienceLevel);
        
        Page<UserProfileDTO> profiles = userProfileService.searchProfiles(
                displayName, location, experienceLevel, pageable);
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/location/{location}")
    @Operation(summary = "Buscar perfis por localização", description = "Retorna perfis de uma localização específica")
    @ApiResponse(responseCode = "200", description = "Lista de perfis da localização")
    public ResponseEntity<List<UserProfileDTO>> getProfilesByLocation(
            @Parameter(description = "Localização") @PathVariable String location) {
        log.info("Buscando perfis por localização: {}", location);
        
        List<UserProfileDTO> profiles = userProfileService.getProfilesByLocation(location);
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/experience/{level}")
    @Operation(summary = "Buscar perfis por nível de experiência", description = "Retorna perfis de um nível de experiência específico")
    @ApiResponse(responseCode = "200", description = "Lista de perfis do nível de experiência")
    public ResponseEntity<List<UserProfileDTO>> getProfilesByExperienceLevel(
            @Parameter(description = "Nível de experiência") @PathVariable UserProfile.ExperienceLevel level) {
        log.info("Buscando perfis por nível de experiência: {}", level);
        
        List<UserProfileDTO> profiles = userProfileService.getProfilesByExperienceLevel(level);
        return ResponseEntity.ok(profiles);
    }

    @PostMapping("/{userId}/profile-picture")
    @Operation(summary = "Upload de foto de perfil", description = "Faz upload da foto de perfil do usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Foto atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Arquivo inválido"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UserProfileDTO> uploadProfilePicture(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId,
            @Parameter(description = "Arquivo de imagem") @RequestParam("file") MultipartFile file) {
        log.info("Recebida solicitação para upload de foto de perfil para usuário ID: {}", userId);
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        // Validar tipo de arquivo
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            log.warn("Tipo de arquivo inválido: {}", contentType);
            return ResponseEntity.badRequest().build();
        }
        
        try {
            UserProfileDTO updatedProfile = userProfileService.updateProfilePicture(userId, file);
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            log.error("Erro ao fazer upload da foto de perfil: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Erro de argumento inválido: {}", e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        log.error("Erro interno do servidor: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno do servidor");
    }
}