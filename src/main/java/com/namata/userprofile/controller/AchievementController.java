package com.namata.userprofile.controller;

import com.namata.userprofile.entity.Achievement;
import com.namata.userprofile.entity.Badge;
import com.namata.userprofile.service.AchievementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/achievements")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Achievements", description = "APIs para gerenciamento de conquistas dos usuários")
public class AchievementController {

    private final AchievementService achievementService;

    @PostMapping("/user/{userId}/badge/{badgeId}")
    @Operation(summary = "Criar conquista", description = "Cria uma nova conquista para um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conquista criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário ou insígnia não encontrados"),
            @ApiResponse(responseCode = "409", description = "Conquista já existe")
    })
    public ResponseEntity<Achievement> createAchievement(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId,
            @Parameter(description = "ID da insígnia") @PathVariable UUID badgeId,
            @Parameter(description = "Descrição da conquista") @RequestParam(required = false) String description,
            @Parameter(description = "Progresso máximo") @RequestParam(required = false) Integer maxProgress,
            @Parameter(description = "Metadados") @RequestBody(required = false) Map<String, Object> metadata) {
        log.info("Criando conquista para usuário ID: {} e insígnia ID: {}", userId, badgeId);

        try {
            Achievement achievement = achievementService.createAchievement(
                    userId, badgeId, description, maxProgress, metadata);
            return ResponseEntity.status(HttpStatus.CREATED).body(achievement);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("já existe")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{achievementId}")
    @Operation(summary = "Buscar conquista por ID", description = "Retorna uma conquista específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conquista encontrada"),
            @ApiResponse(responseCode = "404", description = "Conquista não encontrada")
    })
    public ResponseEntity<Achievement> getAchievementById(
            @Parameter(description = "ID da conquista") @PathVariable UUID achievementId) {
        log.info("Buscando conquista ID: {}", achievementId);

        return achievementService.getAchievementById(achievementId)
                .map(achievement -> ResponseEntity.ok(achievement))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Listar conquistas do usuário", description = "Retorna todas as conquistas de um usuário")
    @ApiResponse(responseCode = "200", description = "Lista de conquistas do usuário")
    public ResponseEntity<List<Achievement>> getUserAchievements(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId) {
        log.info("Buscando conquistas do usuário ID: {}", userId);

        try {
            List<Achievement> achievements = achievementService.getUserAchievements(userId);
            return ResponseEntity.ok(achievements);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}/completed")
    @Operation(summary = "Listar conquistas concluídas", description = "Retorna conquistas concluídas de um usuário")
    @ApiResponse(responseCode = "200", description = "Lista de conquistas concluídas")
    public ResponseEntity<List<Achievement>> getUserCompletedAchievements(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId) {
        log.info("Buscando conquistas completadas do usuário ID: {}", userId);

        try {
            List<Achievement> achievements = achievementService.getUserCompletedAchievements(userId);
            return ResponseEntity.ok(achievements);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}/in-progress")
    @Operation(summary = "Listar conquistas em progresso", description = "Retorna conquistas em progresso de um usuário")
    @ApiResponse(responseCode = "200", description = "Lista de conquistas em progresso")
    public ResponseEntity<List<Achievement>> getUserInProgressAchievements(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId) {
        log.info("Buscando conquistas em progresso do usuário ID: {}", userId);

        try {
            List<Achievement> achievements = achievementService.getUserInProgressAchievements(userId);
            return ResponseEntity.ok(achievements);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}/badge-type/{badgeType}")
    @Operation(summary = "Listar conquistas por tipo de insígnia", description = "Retorna conquistas de um usuário filtradas por tipo de insígnia")
    @ApiResponse(responseCode = "200", description = "Lista de conquistas por tipo de insígnia")
    public ResponseEntity<List<Achievement>> getUserAchievementsByBadgeType(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId,
            @Parameter(description = "Tipo de insígnia") @PathVariable Badge.BadgeType badgeType) {
        log.info("Buscando conquistas do tipo {} para usuário ID: {}", badgeType, userId);

        try {
            List<Achievement> achievements = achievementService.getUserAchievementsByBadgeType(userId, badgeType);
            return ResponseEntity.ok(achievements);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{achievementId}/progress")
    @Operation(summary = "Atualizar progresso", description = "Atualiza o progresso de uma conquista")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Progresso atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Conquista não encontrada")
    })
    public ResponseEntity<Achievement> updateProgress(
            @Parameter(description = "ID da conquista") @PathVariable UUID achievementId,
            @Parameter(description = "Novo progresso") @RequestParam Integer progress) {
        log.info("Atualizando progresso da conquista ID: {} para {}", achievementId, progress);

        try {
            Achievement achievement = achievementService.updateProgress(achievementId, progress);
            return ResponseEntity.ok(achievement);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{achievementId}/increment")
    @Operation(summary = "Incrementar progresso", description = "Incrementa o progresso de uma conquista")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Progresso incrementado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Conquista não encontrada")
    })
    public ResponseEntity<Achievement> incrementProgress(
            @Parameter(description = "ID da conquista") @PathVariable UUID achievementId,
            @Parameter(description = "Incremento") @RequestParam Integer increment) {
        log.info("Incrementando progresso da conquista ID: {} em {}", achievementId, increment);

        try {
            Achievement achievement = achievementService.incrementProgress(achievementId, increment);
            return ResponseEntity.ok(achievement);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{achievementId}/complete")
    @Operation(summary = "Completar conquista", description = "Marca uma conquista como concluída")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conquista concluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Conquista não encontrada")
    })
    public ResponseEntity<Achievement> completeAchievement(
            @Parameter(description = "ID da conquista") @PathVariable UUID achievementId) {
        log.info("Completando conquista ID: {}", achievementId);

        try {
            Achievement achievement = achievementService.completeAchievement(achievementId);
            return ResponseEntity.ok(achievement);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{achievementId}")
    @Operation(summary = "Deletar conquista", description = "Remove uma conquista")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Conquista deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Conquista não encontrada")
    })
    public ResponseEntity<Void> deleteAchievement(
            @Parameter(description = "ID da conquista") @PathVariable UUID achievementId) {
        log.info("Deletando conquista ID: {}", achievementId);

        try {
            achievementService.deleteAchievement(achievementId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/count/completed/user/{userId}")
    @Operation(summary = "Contar conquistas completadas do usuário")
    public ResponseEntity<Long> countUserCompletedAchievements(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId) {
        try {
            long count = achievementService.countUserCompletedAchievements(userId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Erro ao contar conquistas completadas: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/count/total/user/{userId}")
    @Operation(summary = "Contar total de conquistas do usuário")
    public ResponseEntity<Long> countUserTotalAchievements(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId) {
        try {
            long count = achievementService.countUserTotalAchievements(userId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Erro ao contar total de conquistas: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{achievementId}/completion-percentage")
    @Operation(summary = "Percentual de conclusão", description = "Retorna o percentual de conclusão de uma conquista")
    @ApiResponse(responseCode = "200", description = "Percentual de conclusão")
    public ResponseEntity<Double> getCompletionPercentage(
            @Parameter(description = "ID da conquista") @PathVariable UUID achievementId) {
        log.info("Obtendo porcentagem de conclusão da conquista ID: {}", achievementId);

        try {
            Double percentage = achievementService.getCompletionPercentage(achievementId);
            return ResponseEntity.ok(percentage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/user/{userId}/check-trail-achievements")
    @Operation(summary = "Verificar conquistas de trilhas", description = "Verifica e cria conquistas automáticas baseadas em trilhas completadas")
    @ApiResponse(responseCode = "204", description = "Verificação concluída")
    public ResponseEntity<Void> checkTrailAchievements(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId,
            @Parameter(description = "Número de trilhas completadas") @RequestParam Integer trailsCompleted) {
        log.info("Verificando conquistas de trilhas para usuário ID: {} com {} trilhas", userId, trailsCompleted);

        try {
            achievementService.checkAndCreateTrailAchievements(userId, trailsCompleted);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/user/{userId}/check-distance-achievements")
    @Operation(summary = "Verificar conquistas de distância", description = "Verifica e cria conquistas automáticas baseadas em distância percorrida")
    @ApiResponse(responseCode = "204", description = "Verificação concluída")
    public ResponseEntity<Void> checkDistanceAchievements(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId,
            @Parameter(description = "Distância total percorrida") @RequestParam Double totalDistance) {
        log.info("Verificando conquistas de distância para usuário ID: {} com {} km", userId, totalDistance);

        try {
            achievementService.checkAndCreateDistanceAchievements(userId, totalDistance);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
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