package com.namata.userprofile.controller;

import com.namata.userprofile.dto.SaveTrailRequest;
import com.namata.userprofile.dto.UserSavedTrailDTO;
import com.namata.userprofile.service.UserSavedTrailService;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/saved-trails")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Saved Trails", description = "APIs para gerenciamento de trilhas salvas pelos usuários")
public class UserSavedTrailController {

    private final UserSavedTrailService userSavedTrailService;

    @PostMapping("/user/{userId}")
    @Operation(summary = "Salvar trilha", description = "Salva uma trilha para o usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Trilha salva com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou trilha já salva"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UserSavedTrailDTO> saveTrail(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId,
            @Valid @RequestBody SaveTrailRequest request) {
        log.info("Recebida solicitação para salvar trilha {} para usuário ID: {}", request.getTrailId(), userId);
        
        try {
            UserSavedTrailDTO savedTrail = userSavedTrailService.saveTrail(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTrail);
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao salvar trilha: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro interno ao salvar trilha: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/user/{userId}/trail/{trailId}")
    @Operation(summary = "Remover trilha salva", description = "Remove uma trilha da lista de trilhas salvas do usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Trilha removida com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário ou trilha salva não encontrada")
    })
    public ResponseEntity<Void> unsaveTrail(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId,
            @Parameter(description = "ID da trilha") @PathVariable UUID trailId) {
        log.info("Recebida solicitação para remover trilha {} salva do usuário ID: {}", trailId, userId);
        
        try {
            userSavedTrailService.unsaveTrail(userId, trailId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao remover trilha salva: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Listar trilhas salvas", description = "Retorna todas as trilhas salvas pelo usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de trilhas salvas"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<List<UserSavedTrailDTO>> getSavedTrails(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId) {
        log.info("Buscando trilhas salvas do usuário ID: {}", userId);
        
        try {
            List<UserSavedTrailDTO> savedTrails = userSavedTrailService.getSavedTrails(userId);
            return ResponseEntity.ok(savedTrails);
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao buscar trilhas salvas: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}/paginated")
    @Operation(summary = "Listar trilhas salvas paginadas", description = "Retorna as trilhas salvas pelo usuário com paginação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Página de trilhas salvas"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Page<UserSavedTrailDTO>> getSavedTrailsPaginated(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Buscando trilhas salvas paginadas do usuário ID: {}", userId);
        
        try {
            Page<UserSavedTrailDTO> savedTrailsPage = userSavedTrailService.getSavedTrailsPaginated(userId, pageable);
            return ResponseEntity.ok(savedTrailsPage);
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao buscar trilhas salvas paginadas: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}/trail/{trailId}/is-saved")
    @Operation(summary = "Verificar se trilha está salva", description = "Verifica se uma trilha específica está salva pelo usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status da trilha salva"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Boolean> isTrailSaved(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId,
            @Parameter(description = "ID da trilha") @PathVariable UUID trailId) {
        log.info("Verificando se trilha {} está salva pelo usuário ID: {}", trailId, userId);
        
        try {
            boolean isSaved = userSavedTrailService.isTrailSaved(userId, trailId);
            return ResponseEntity.ok(isSaved);
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao verificar trilha salva: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}/trail/{trailId}/details")
    @Operation(summary = "Obter detalhes da trilha salva", description = "Retorna os detalhes da trilha salva pelo usuário, incluindo o ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Detalhes da trilha salva"),
        @ApiResponse(responseCode = "404", description = "Usuário ou trilha salva não encontrada")
    })
    public ResponseEntity<UserSavedTrailDTO> getSavedTrailDetails(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId,
            @Parameter(description = "ID da trilha") @PathVariable UUID trailId) {
        log.info("Buscando detalhes da trilha {} salva pelo usuário ID: {}", trailId, userId);
        
        try {
            Optional<UserSavedTrailDTO> savedTrail = userSavedTrailService.getSavedTrailDetails(userId, trailId);
            return savedTrail.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao buscar detalhes da trilha salva: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}/count")
    @Operation(summary = "Contar trilhas salvas", description = "Retorna o número total de trilhas salvas pelo usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Número de trilhas salvas"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Long> getSavedTrailsCount(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId) {
        log.info("Contando trilhas salvas do usuário ID: {}", userId);
        
        try {
            long count = userSavedTrailService.getSavedTrailsCount(userId);
            return ResponseEntity.ok(count);
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao contar trilhas salvas: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}/trail-ids")
    @Operation(summary = "Listar IDs das trilhas salvas", description = "Retorna apenas os IDs das trilhas salvas pelo usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de IDs das trilhas salvas"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<List<UUID>> getSavedTrailIds(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId) {
        log.info("Buscando IDs das trilhas salvas do usuário ID: {}", userId);
        
        try {
            List<UUID> trailIds = userSavedTrailService.getSavedTrailIds(userId);
            return ResponseEntity.ok(trailIds);
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao buscar IDs das trilhas salvas: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}