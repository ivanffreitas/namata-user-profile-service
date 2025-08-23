package com.namata.userprofile.controller;

import com.namata.userprofile.dto.ActivityDTO;
import com.namata.userprofile.entity.Activity;
import com.namata.userprofile.service.ActivityService;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/activities")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Activities", description = "APIs para gerenciamento de atividades dos usuários")
public class ActivityController {

    private final ActivityService activityService;

    @PostMapping("/user/{userId}")
    @Operation(summary = "Criar atividade", description = "Cria uma nova atividade para um usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Atividade criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<ActivityDTO> createActivity(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId,
            @Valid @RequestBody ActivityDTO activityDTO) {
        log.info("Criando atividade para usuário ID: {}", userId);
        
        try {
            ActivityDTO activity = activityService.createActivity(userId, activityDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(activity);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{activityId}")
    @Operation(summary = "Buscar atividade por ID", description = "Retorna uma atividade específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Atividade encontrada"),
        @ApiResponse(responseCode = "404", description = "Atividade não encontrada")
    })
    public ResponseEntity<ActivityDTO> getActivityById(
            @Parameter(description = "ID da atividade") @PathVariable UUID activityId) {
        log.info("Buscando atividade ID: {}", activityId);
        
        return activityService.getActivityById(activityId)
                .map(activity -> ResponseEntity.ok(activity))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Listar atividades do usuário", description = "Retorna todas as atividades de um usuário com paginação")
    @ApiResponse(responseCode = "200", description = "Lista de atividades do usuário")
    public ResponseEntity<Page<ActivityDTO>> getUserActivities(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Buscando atividades do usuário ID: {}", userId);
        
        try {
            Page<ActivityDTO> activities = activityService.getUserActivities(userId, pageable);
            return ResponseEntity.ok(activities);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}/type/{type}")
    @Operation(summary = "Listar atividades por tipo", description = "Retorna atividades de um usuário filtradas por tipo")
    @ApiResponse(responseCode = "200", description = "Lista de atividades por tipo")
    public ResponseEntity<List<ActivityDTO>> getUserActivitiesByType(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId,
            @Parameter(description = "Tipo de atividade") @PathVariable Activity.ActivityType type) {
        log.info("Buscando atividades do tipo {} para usuário ID: {}", type, userId);
        
        try {
            List<ActivityDTO> activities = activityService.getUserActivitiesByType(userId, type);
            return ResponseEntity.ok(activities);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/public")
    @Operation(summary = "Listar atividades públicas", description = "Retorna todas as atividades públicas")
    @ApiResponse(responseCode = "200", description = "Lista de atividades públicas")
    public ResponseEntity<List<ActivityDTO>> getPublicActivities() {
        log.info("Buscando atividades públicas");
        
        List<ActivityDTO> activities = activityService.getPublicActivities();
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/recent")
    @Operation(summary = "Listar atividades recentes", description = "Retorna as atividades mais recentes")
    @ApiResponse(responseCode = "200", description = "Lista de atividades recentes")
    public ResponseEntity<List<ActivityDTO>> getRecentActivities(
            @Parameter(description = "Limite de resultados") @RequestParam(defaultValue = "10") int limit) {
        log.info("Buscando {} atividades recentes", limit);
        
        List<ActivityDTO> activities = activityService.getRecentActivities(limit);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/trail/{trailId}")
    @Operation(summary = "Listar atividades por trilha", description = "Retorna atividades de uma trilha específica")
    @ApiResponse(responseCode = "200", description = "Lista de atividades da trilha")
    public ResponseEntity<List<ActivityDTO>> getActivitiesByTrail(
            @Parameter(description = "ID da trilha") @PathVariable UUID trailId) {
        log.info("Buscando atividades da trilha ID: {}", trailId);
        
        List<ActivityDTO> activities = activityService.getActivitiesByTrail(trailId);
        return ResponseEntity.ok(activities);
    }

    @PutMapping("/{activityId}")
    @Operation(summary = "Atualizar atividade", description = "Atualiza os dados de uma atividade")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Atividade atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Atividade não encontrada")
    })
    public ResponseEntity<ActivityDTO> updateActivity(
            @Parameter(description = "ID da atividade") @PathVariable UUID activityId,
            @Valid @RequestBody ActivityDTO activityDTO) {
        log.info("Atualizando atividade ID: {}", activityId);
        
        try {
            ActivityDTO updatedActivity = activityService.updateActivity(activityId, activityDTO);
            return ResponseEntity.ok(updatedActivity);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{activityId}")
    @Operation(summary = "Deletar atividade", description = "Remove uma atividade")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Atividade deletada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Atividade não encontrada")
    })
    public ResponseEntity<Void> deleteActivity(
            @Parameter(description = "ID da atividade") @PathVariable UUID activityId) {
        log.info("Deletando atividade ID: {}", activityId);
        
        try {
            activityService.deleteActivity(activityId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{activityId}/like")
    @Operation(summary = "Curtir atividade", description = "Adiciona uma curtida à atividade")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Atividade curtida com sucesso"),
        @ApiResponse(responseCode = "404", description = "Atividade não encontrada")
    })
    public ResponseEntity<ActivityDTO> likeActivity(
            @Parameter(description = "ID da atividade") @PathVariable UUID activityId) {
        log.info("Curtindo atividade ID: {}", activityId);
        
        try {
            ActivityDTO activity = activityService.likeActivity(activityId);
            return ResponseEntity.ok(activity);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{activityId}/comment")
    @Operation(summary = "Comentar atividade", description = "Adiciona um comentário à atividade")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comentário adicionado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Atividade não encontrada")
    })
    public ResponseEntity<ActivityDTO> addComment(
            @Parameter(description = "ID da atividade") @PathVariable UUID activityId) {
        log.info("Adicionando comentário à atividade ID: {}", activityId);
        
        try {
            ActivityDTO activity = activityService.addComment(activityId);
            return ResponseEntity.ok(activity);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/count/user/{userId}")
    @Operation(summary = "Contar atividades do usuário")
    public ResponseEntity<Long> countUserActivities(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId) {
        try {
            long count = activityService.countUserActivities(userId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Erro ao contar atividades do usuário: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/count/user/{userId}/type/{type}")
    @Operation(summary = "Contar atividades do usuário por tipo")
    public ResponseEntity<Long> countUserActivitiesByType(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId,
            @Parameter(description = "Tipo da atividade") @PathVariable Activity.ActivityType type) {
        try {
            long count = activityService.countUserActivitiesByType(userId, type);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Erro ao contar atividades do usuário por tipo: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}/distance")
    @Operation(summary = "Distância total do usuário", description = "Retorna a distância total percorrida pelo usuário")
    @ApiResponse(responseCode = "200", description = "Distância total em km")
    public ResponseEntity<Double> getTotalDistanceByUser(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId) {
        log.info("Calculando distância total do usuário ID: {}", userId);
        
        try {
            Double totalDistance = activityService.getTotalDistanceByUser(userId);
            return ResponseEntity.ok(totalDistance != null ? totalDistance : 0.0);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}/duration")
    @Operation(summary = "Tempo total do usuário", description = "Retorna o tempo total gasto pelo usuário em atividades")
    @ApiResponse(responseCode = "200", description = "Tempo total em minutos")
    public ResponseEntity<Integer> getTotalDurationByUser(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId) {
        log.info("Calculando tempo total do usuário ID: {}", userId);
        
        try {
            Integer totalDuration = activityService.getTotalDurationByUser(userId);
            return ResponseEntity.ok(totalDuration != null ? totalDuration : 0);
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