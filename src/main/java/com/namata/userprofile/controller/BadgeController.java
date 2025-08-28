package com.namata.userprofile.controller;

import com.namata.userprofile.dto.BadgeDTO;
import com.namata.userprofile.entity.Badge;
import com.namata.userprofile.service.BadgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/badges")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Badges", description = "APIs para gerenciamento de insígnias do sistema")
public class BadgeController {

    private final BadgeService badgeService;

    @PostMapping
    @Operation(summary = "Criar insígnia", description = "Cria uma nova insígnia no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Insígnia criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "409", description = "Insígnia com este nome já existe")
    })
    public ResponseEntity<Badge> createBadge(@Valid @RequestBody Badge badge) {
        log.info("Criando nova insígnia: {}", badge.getName());
        
        try {
            Badge createdBadge = badgeService.createBadge(badge.getName(), badge.getDescription(), 
                    badge.getIconUrl(), badge.getType(), badge.getRarity(), 
                    badge.getPointsRequired(), badge.getMaxProgress(), badge.getCriteria());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBadge);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("já existe")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{badgeId}")
    @Operation(summary = "Buscar insígnia por ID", description = "Retorna uma insígnia específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Insígnia encontrada"),
        @ApiResponse(responseCode = "404", description = "Insígnia não encontrada")
    })
    public ResponseEntity<Badge> getBadgeById(
            @Parameter(description = "ID da insígnia") @PathVariable UUID badgeId) {
        log.info("Buscando insígnia ID: {}", badgeId);
        
        return badgeService.getBadgeById(badgeId)
                .map(badge -> ResponseEntity.ok(badge))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Buscar insígnia por nome", description = "Retorna uma insígnia pelo nome")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Insígnia encontrada"),
        @ApiResponse(responseCode = "404", description = "Insígnia não encontrada")
    })
    public ResponseEntity<Badge> getBadgeByName(
            @Parameter(description = "Nome da insígnia") @PathVariable String name) {
        log.info("Buscando insígnia por nome: {}", name);
        
        return badgeService.getBadgeByName(name)
                .map(badge -> ResponseEntity.ok(badge))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    @Operation(summary = "Listar insígnias ativas", description = "Retorna todas as insígnias ativas ordenadas por data de criação")
    @ApiResponse(responseCode = "200", description = "Lista de insígnias ativas")
    public ResponseEntity<Page<Badge>> getActiveBadges(Pageable pageable) {
        log.info("Buscando insígnias ativas");
        
        List<Badge> allBadges = badgeService.getAllActiveBadges();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allBadges.size());
        List<Badge> pageContent = allBadges.subList(start, end);
        Page<Badge> badges = new PageImpl<>(pageContent, pageable, allBadges.size());
        return ResponseEntity.ok(badges);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Listar insígnias por tipo", description = "Retorna insígnias filtradas por tipo")
    @ApiResponse(responseCode = "200", description = "Lista de insígnias por tipo")
    public ResponseEntity<List<Badge>> getBadgesByType(
            @Parameter(description = "Tipo de insígnia") @PathVariable Badge.BadgeType type) {
        log.info("Buscando insígnias do tipo: {}", type);
        
        List<Badge> badges = badgeService.getBadgesByType(type);
        return ResponseEntity.ok(badges);
    }

    @GetMapping("/rarity/{rarity}")
    @Operation(summary = "Listar insígnias por raridade", description = "Retorna insígnias filtradas por raridade")
    @ApiResponse(responseCode = "200", description = "Lista de insígnias por raridade")
    public ResponseEntity<List<Badge>> getBadgesByRarity(
            @Parameter(description = "Raridade da insígnia") @PathVariable Badge.Rarity rarity) {
        log.info("Buscando insígnias da raridade: {}", rarity);
        
        List<Badge> badges = badgeService.getBadgesByRarity(rarity);
        return ResponseEntity.ok(badges);
    }

    @GetMapping("/type/{type}/rarity/{rarity}")
    @Operation(summary = "Listar insígnias por tipo e raridade", description = "Retorna insígnias filtradas por tipo e raridade")
    @ApiResponse(responseCode = "200", description = "Lista de insígnias por tipo e raridade")
    public ResponseEntity<List<Badge>> getBadgesByTypeAndRarity(
            @Parameter(description = "Tipo de insígnia") @PathVariable Badge.BadgeType type,
            @Parameter(description = "Raridade da insígnia") @PathVariable Badge.Rarity rarity) {
        log.info("Buscando insígnias do tipo {} e raridade {}", type, rarity);
        
        List<Badge> badges = badgeService.getBadgesByTypeAndRarity(type, rarity);
        return ResponseEntity.ok(badges);
    }

    @GetMapping("/available-for-points/{points}")
    @Operation(summary = "Listar insígnias disponíveis por pontos", description = "Retorna insígnias que podem ser obtidas com os pontos especificados")
    @ApiResponse(responseCode = "200", description = "Lista de insígnias disponíveis")
    public ResponseEntity<List<Badge>> getAvailableBadgesForPoints(
            @Parameter(description = "Pontos disponíveis") @PathVariable Integer points) {
        log.info("Buscando insígnias disponíveis para {} pontos", points);
        
        List<Badge> badges = badgeService.getAvailableBadgesByPoints(points);
        return ResponseEntity.ok(badges);
    }

    @PutMapping("/{badgeId}")
    @Operation(summary = "Atualizar insígnia", description = "Atualiza uma insígnia existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Insígnia atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Insígnia não encontrada")
    })
    public ResponseEntity<Badge> updateBadge(
            @Parameter(description = "ID da insígnia") @PathVariable UUID badgeId,
            @Valid @RequestBody Badge badge) {
        log.info("Atualizando insígnia ID: {}", badgeId);
        
        try {
            Badge updatedBadge = badgeService.updateBadge(badgeId, badge.getName(), badge.getDescription(), 
                    badge.getIconUrl(), badge.getType(), badge.getRarity(), 
                    badge.getPointsRequired(), badge.getMaxProgress(), badge.getCriteria());
            return ResponseEntity.ok(updatedBadge);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{badgeId}/deactivate")
    @Operation(summary = "Desativar insígnia", description = "Desativa uma insígnia")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Insígnia desativada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Insígnia não encontrada")
    })
    public ResponseEntity<Badge> deactivateBadge(
            @Parameter(description = "ID da insígnia") @PathVariable UUID badgeId) {
        log.info("Desativando insígnia ID: {}", badgeId);
        
        try {
            Badge badge = badgeService.deactivateBadge(badgeId);
            return ResponseEntity.ok(badge);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{badgeId}/activate")
    @Operation(summary = "Ativar insígnia", description = "Ativa uma insígnia")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Insígnia ativada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Insígnia não encontrada")
    })
    public ResponseEntity<Badge> activateBadge(
            @Parameter(description = "ID da insígnia") @PathVariable UUID badgeId) {
        log.info("Ativando insígnia ID: {}", badgeId);
        
        try {
            Badge badge = badgeService.activateBadge(badgeId);
            return ResponseEntity.ok(badge);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/count/active")
    @Operation(summary = "Contar insígnias ativas", description = "Retorna o número de insígnias ativas")
    @ApiResponse(responseCode = "200", description = "Número de insígnias ativas")
    public ResponseEntity<Long> countActiveBadges() {
        try {
            long count = badgeService.countActiveBadges();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Erro ao contar badges ativos: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/count/type/{type}")
    @Operation(summary = "Contar badges por tipo")
    public ResponseEntity<Long> countBadgesByType(
            @Parameter(description = "Tipo do badge") @PathVariable Badge.BadgeType type) {
        try {
            long count = badgeService.countBadgesByType(type);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Erro ao contar badges por tipo: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/simple")
    @Operation(summary = "Listar insígnias simples", description = "Retorna todas as insígnias ativas em formato simplificado")
    @ApiResponse(responseCode = "200", description = "Lista de insígnias simplificadas")
    public ResponseEntity<List<BadgeDTO>> getSimpleBadges() {
        log.info("Buscando insígnias em formato simplificado");
        
        try {
            List<Badge> badges = badgeService.getAllActiveBadges();
            List<BadgeDTO> badgeDTOs = badges.stream()
                    .map(BadgeDTO::fromEntity)
                    .toList();
            return ResponseEntity.ok(badgeDTOs);
        } catch (Exception e) {
            log.error("Erro ao buscar badges simplificados: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/create-default-badges")
    @Operation(summary = "Criar insígnias padrão", description = "Cria as insígnias padrão do sistema")
    @ApiResponse(responseCode = "204", description = "Insígnias padrão criadas com sucesso")
    public ResponseEntity<Void> createDefaultBadges() {
        log.info("Criando insígnias padrão do sistema");
        
        badgeService.createDefaultBadges();
        return ResponseEntity.noContent().build();
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