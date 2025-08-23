package com.namata.userprofile.controller;

import com.namata.userprofile.dto.StatisticsDTO;
import com.namata.userprofile.entity.Statistics;
import com.namata.userprofile.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Statistics", description = "APIs para gerenciamento de estatísticas dos usuários")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/user/{userId}")
    @Operation(summary = "Buscar estatísticas do usuário", description = "Retorna as estatísticas de um usuário específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estatísticas encontradas"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<StatisticsDTO> getUserStatistics(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId) {
        log.info("Buscando estatísticas do usuário ID: {}", userId);
        
        try {
            StatisticsDTO statistics = statisticsService.getStatisticsByUserId(userId);
            return ResponseEntity.ok(statistics);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{statisticsId}")
    @Operation(summary = "Buscar estatísticas por ID", description = "Retorna estatísticas específicas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estatísticas encontradas"),
        @ApiResponse(responseCode = "404", description = "Estatísticas não encontradas")
    })
    public ResponseEntity<StatisticsDTO> getStatisticsById(
            @Parameter(description = "ID das estatísticas") @PathVariable UUID statisticsId) {
        log.info("Buscando estatísticas ID: {}", statisticsId);
        
        return statisticsService.getStatisticsById(statisticsId)
                .map(statistics -> ResponseEntity.ok(statisticsService.convertToDTO(statistics)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/user/{userId}/trails")
    @Operation(summary = "Atualizar estatísticas de trilhas", description = "Atualiza as estatísticas de trilhas de um usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estatísticas atualizadas com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<StatisticsDTO> updateTrailStatistics(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId,
            @Parameter(description = "Distância da trilha") @RequestParam Double distance,
            @Parameter(description = "Duração da trilha (minutos)") @RequestParam Integer duration,
            @Parameter(description = "Ganho de elevação") @RequestParam Double elevationGain) {
        log.info("Atualizando estatísticas de trilhas para usuário ID: {}", userId);
        
        try {
            StatisticsDTO statistics = statisticsService.updateTrailStatistics(
                userId, null, distance, duration, null, null, null);
            return ResponseEntity.ok(statistics);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/user/{userId}/activities")
    @Operation(summary = "Atualizar estatísticas de atividades", description = "Atualiza as estatísticas de atividades de um usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estatísticas atualizadas com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<StatisticsDTO> updateActivityStatistics(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId,
            @Parameter(description = "Incremento de fotos") @RequestParam(defaultValue = "0") Integer photosIncrement,
            @Parameter(description = "Incremento de avaliações") @RequestParam(defaultValue = "0") Integer reviewsIncrement,
            @Parameter(description = "Incremento de curtidas") @RequestParam(defaultValue = "0") Integer likesIncrement,
            @Parameter(description = "Incremento de comentários") @RequestParam(defaultValue = "0") Integer commentsIncrement) {
        log.info("Atualizando estatísticas de atividades para usuário ID: {}", userId);
        
        try {
            StatisticsDTO statistics = statisticsService.updateActivityStatistics(
                userId, photosIncrement, reviewsIncrement, likesIncrement, commentsIncrement);
            return ResponseEntity.ok(statistics);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/user/{userId}/achievements")
    @Operation(summary = "Atualizar estatísticas de conquistas", description = "Atualiza as estatísticas de conquistas de um usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estatísticas atualizadas com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<StatisticsDTO> updateAchievementStatistics(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId,
            @Parameter(description = "Incremento de insígnias") @RequestParam(defaultValue = "0") Integer badgesIncrement,
            @Parameter(description = "Incremento de pontos") @RequestParam(defaultValue = "0") Integer pointsIncrement,
            @Parameter(description = "Incremento de sequência atual") @RequestParam(defaultValue = "0") Integer currentStreakIncrement) {
        log.info("Atualizando estatísticas de conquistas para usuário ID: {}", userId);
        
        try {
            StatisticsDTO statistics = statisticsService.updateAchievementStatistics(
                userId, badgesIncrement, pointsIncrement, currentStreakIncrement, null);
            return ResponseEntity.ok(statistics);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/user/{userId}/social")
    @Operation(summary = "Atualizar estatísticas sociais", description = "Atualiza as estatísticas sociais de um usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estatísticas atualizadas com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<StatisticsDTO> updateSocialStatistics(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId,
            @Parameter(description = "Incremento de seguidores") @RequestParam(defaultValue = "0") Integer followersIncrement,
            @Parameter(description = "Incremento de seguindo") @RequestParam(defaultValue = "0") Integer followingIncrement,
            @Parameter(description = "Incremento de guias reservados") @RequestParam(defaultValue = "0") Integer guidesBookedIncrement) {
        log.info("Atualizando estatísticas sociais para usuário ID: {}", userId);
        
        try {
            StatisticsDTO statistics = statisticsService.updateSocialStatistics(
                userId, followersIncrement, followingIncrement, guidesBookedIncrement);
            return ResponseEntity.ok(statistics);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/user/{userId}/ranking")
    @Operation(summary = "Atualizar ranking", description = "Atualiza o ranking de um usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ranking atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<StatisticsDTO> updateRanking(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId,
            @Parameter(description = "Posição no ranking global") @RequestParam(required = false) Integer globalRank,
            @Parameter(description = "Posição no ranking local") @RequestParam(required = false) Integer localRank) {
        log.info("Atualizando ranking para usuário ID: {}", userId);
        
        try {
            StatisticsDTO statistics = statisticsService.updateRanking(userId, globalRank, localRank);
            return ResponseEntity.ok(statistics);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/user/{userId}/last-activity")
    @Operation(summary = "Atualizar última atividade", description = "Atualiza o timestamp da última atividade do usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Última atividade atualizada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<StatisticsDTO> updateLastActivity(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId) {
        log.info("Atualizando última atividade para usuário ID: {}", userId);
        
        try {
            StatisticsDTO statistics = statisticsService.updateLastActivity(userId);
            return ResponseEntity.ok(statistics);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/user/{userId}/increment-trails")
    @Operation(summary = "Incrementar trilhas completadas", description = "Incrementa o contador de trilhas completadas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trilhas incrementadas com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<StatisticsDTO> incrementTrailsCompleted(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId,
            @Parameter(description = "Número de trilhas a incrementar") @RequestParam(defaultValue = "1") Integer increment) {
        log.info("Incrementando {} trilhas completadas para usuário ID: {}", increment, userId);
        
        try {
            StatisticsDTO statistics = statisticsService.incrementTrailsCompleted(userId, increment);
            return ResponseEntity.ok(statistics);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/ranking/points")
    @Operation(summary = "Ranking por pontos", description = "Retorna o ranking de usuários ordenado por pontos totais")
    @ApiResponse(responseCode = "200", description = "Ranking por pontos")
    public ResponseEntity<Page<StatisticsDTO>> getRankingByPoints(Pageable pageable) {
        log.info("Buscando ranking por pontos");
        
        Page<StatisticsDTO> ranking = statisticsService.getRankingByPoints(pageable);
        return ResponseEntity.ok(ranking);
    }

    @GetMapping("/ranking/trails")
    @Operation(summary = "Ranking por trilhas", description = "Retorna o ranking de usuários ordenado por trilhas completadas")
    @ApiResponse(responseCode = "200", description = "Ranking por trilhas")
    public ResponseEntity<Page<StatisticsDTO>> getRankingByTrails(Pageable pageable) {
        log.info("Buscando ranking por trilhas");
        
        Page<StatisticsDTO> ranking = statisticsService.getRankingByTrails(pageable);
        return ResponseEntity.ok(ranking);
    }

    @GetMapping("/ranking/distance")
    @Operation(summary = "Ranking por distância", description = "Retorna o ranking de usuários ordenado por distância total")
    @ApiResponse(responseCode = "200", description = "Ranking por distância")
    public ResponseEntity<Page<StatisticsDTO>> getRankingByDistance(Pageable pageable) {
        log.info("Buscando ranking por distância");
        
        Page<StatisticsDTO> ranking = statisticsService.getRankingByDistance(pageable);
        return ResponseEntity.ok(ranking);
    }

    @GetMapping("/ranking/location/{location}")
    @Operation(summary = "Ranking por localização", description = "Retorna o ranking de usuários de uma localização específica")
    @ApiResponse(responseCode = "200", description = "Ranking por localização")
    public ResponseEntity<List<StatisticsDTO>> getRankingByLocation(
            @Parameter(description = "Localização") @PathVariable String location) {
        log.info("Buscando ranking por localização: {}", location);
        
        List<StatisticsDTO> ranking = statisticsService.getRankingByLocation(location);
        return ResponseEntity.ok(ranking);
    }

    @GetMapping("/averages/points")
    @Operation(summary = "Média de pontos", description = "Retorna a média de pontos de todos os usuários")
    @ApiResponse(responseCode = "200", description = "Média de pontos")
    public ResponseEntity<Double> getAveragePoints() {
        log.info("Calculando média de pontos");
        
        Double average = statisticsService.getAveragePoints();
        return ResponseEntity.ok(average);
    }

    @GetMapping("/averages/distance")
    @Operation(summary = "Média de distância", description = "Retorna a média de distância de todos os usuários")
    @ApiResponse(responseCode = "200", description = "Média de distância")
    public ResponseEntity<Double> getAverageDistance() {
        log.info("Calculando média de distância");
        
        Double average = statisticsService.getAverageDistance();
        return ResponseEntity.ok(average);
    }

    @GetMapping("/averages/trails")
    @Operation(summary = "Média de trilhas", description = "Retorna a média de trilhas completadas de todos os usuários")
    @ApiResponse(responseCode = "200", description = "Média de trilhas")
    public ResponseEntity<Double> getAverageTrailsCompleted() {
        log.info("Calculando média de trilhas completadas");
        
        Double average = statisticsService.getAverageTrailsCompleted();
        return ResponseEntity.ok(average);
    }

    @GetMapping("/max/points")
    @Operation(summary = "Máximo de pontos", description = "Retorna o máximo de pontos entre todos os usuários")
    @ApiResponse(responseCode = "200", description = "Máximo de pontos")
    public ResponseEntity<Integer> getMaxPoints() {
        log.info("Buscando máximo de pontos");
        
        Integer max = statisticsService.getMaxPoints();
        return ResponseEntity.ok(max);
    }

    @GetMapping("/max/distance")
    @Operation(summary = "Máxima distância", description = "Retorna a máxima distância entre todos os usuários")
    @ApiResponse(responseCode = "200", description = "Máxima distância")
    public ResponseEntity<Double> getMaxDistance() {
        log.info("Buscando máxima distância");
        
        Double max = statisticsService.getMaxDistance();
        return ResponseEntity.ok(max);
    }

    @GetMapping("/max/trails")
    @Operation(summary = "Máximo de trilhas", description = "Retorna o máximo de trilhas completadas entre todos os usuários")
    @ApiResponse(responseCode = "200", description = "Máximo de trilhas")
    public ResponseEntity<Integer> getMaxTrailsCompleted() {
        log.info("Buscando máximo de trilhas completadas");
        
        Integer max = statisticsService.getMaxTrailsCompleted();
        return ResponseEntity.ok(max);
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