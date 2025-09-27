package com.namata.userprofile.service;

import com.namata.userprofile.dto.SaveTrailRequest;
import com.namata.userprofile.dto.UserSavedTrailDTO;
import com.namata.userprofile.entity.UserProfile;
import com.namata.userprofile.entity.UserSavedTrail;
import com.namata.userprofile.repository.UserProfileRepository;
import com.namata.userprofile.repository.UserSavedTrailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserSavedTrailService {

    private final UserSavedTrailRepository userSavedTrailRepository;
    private final UserProfileRepository userProfileRepository;

    public UserSavedTrailDTO saveTrail(UUID userId, SaveTrailRequest request) {
        log.info("Salvando trilha {} para usuário ID: {}", request.getTrailId(), userId);

        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        // Verificar se a trilha já está salva e ativa
        if (userSavedTrailRepository.existsByUserProfileAndTrailIdAndIsActiveTrue(userProfile, request.getTrailId())) {
            throw new IllegalArgumentException("Trilha já está salva pelo usuário");
        }

        // Verificar se existe um registro inativo para reativar
        Optional<UserSavedTrail> existingTrail = userSavedTrailRepository.findByUserProfileAndTrailId(userProfile, request.getTrailId());
        
        if (existingTrail.isPresent()) {
            // Reativar trilha existente
            UserSavedTrail trail = existingTrail.get();
            trail.setIsActive(true);
            trail.setNotes(request.getNotes()); // Atualizar notas se fornecidas
            
            UserSavedTrail saved = userSavedTrailRepository.save(trail);
            log.info("Trilha {} reativada com sucesso para usuário ID: {}", request.getTrailId(), userId);
            return convertToDTO(saved);
        } else {
            // Criar novo registro
            UserSavedTrail savedTrail = UserSavedTrail.builder()
                    .userProfile(userProfile)
                    .trailId(request.getTrailId())
                    .notes(request.getNotes())
                    .isActive(true)
                    .build();

            try {
                UserSavedTrail saved = userSavedTrailRepository.save(savedTrail);
                log.info("Trilha {} salva com sucesso para usuário ID: {}", request.getTrailId(), userId);
                return convertToDTO(saved);
            } catch (DataIntegrityViolationException e) {
                log.warn("Tentativa de salvar trilha duplicada: {} para usuário ID: {}", request.getTrailId(), userId);
                throw new IllegalArgumentException("Trilha já está salva pelo usuário");
            }
        }
    }

    public void unsaveTrail(UUID userId, UUID trailId) {
        log.info("Removendo trilha {} salva do usuário ID: {}", trailId, userId);

        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        UserSavedTrail savedTrail = userSavedTrailRepository
                .findByUserProfileAndTrailIdAndIsActiveTrue(userProfile, trailId)
                .orElseThrow(() -> new IllegalArgumentException("Trilha salva não encontrada"));

        // Soft delete
        savedTrail.setIsActive(false);
        userSavedTrailRepository.save(savedTrail);
        
        log.info("Trilha {} removida com sucesso do usuário ID: {}", trailId, userId);
    }

    @Transactional(readOnly = true)
    public List<UserSavedTrailDTO> getSavedTrails(UUID userId) {
        log.info("Buscando trilhas salvas do usuário ID: {}", userId);

        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        List<UserSavedTrail> savedTrails = userSavedTrailRepository
                .findByUserProfileAndIsActiveTrueOrderBySavedAtDesc(userProfile);

        return savedTrails.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<UserSavedTrailDTO> getSavedTrailsPaginated(UUID userId, Pageable pageable) {
        log.info("Buscando trilhas salvas paginadas do usuário ID: {}", userId);

        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        Page<UserSavedTrail> savedTrailsPage = userSavedTrailRepository
                .findByUserProfileAndIsActiveTrueOrderBySavedAtDesc(userProfile, pageable);

        List<UserSavedTrailDTO> dtos = savedTrailsPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, savedTrailsPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public boolean isTrailSaved(UUID userId, UUID trailId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        return userSavedTrailRepository.existsByUserProfileAndTrailIdAndIsActiveTrue(userProfile, trailId);
    }

    @Transactional(readOnly = true)
    public long getSavedTrailsCount(UUID userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        return userSavedTrailRepository.countByUserProfileAndIsActiveTrue(userProfile);
    }

    @Transactional(readOnly = true)
    public List<UUID> getSavedTrailIds(UUID userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        return userSavedTrailRepository.findTrailIdsByUserProfileAndIsActiveTrueOrderBySavedAtDesc(userProfile);
    }

    @Transactional(readOnly = true)
    public Optional<UserSavedTrailDTO> getSavedTrailDetails(UUID userId, UUID trailId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        Optional<UserSavedTrail> savedTrail = userSavedTrailRepository.findByUserProfileAndTrailIdAndIsActiveTrue(userProfile, trailId);
        return savedTrail.map(this::convertToDTO);
    }

    private UserSavedTrailDTO convertToDTO(UserSavedTrail savedTrail) {
        UserSavedTrailDTO.UserSavedTrailDTOBuilder builder = UserSavedTrailDTO.builder()
                .id(savedTrail.getId())
                .userProfileId(savedTrail.getUserProfile().getId())
                .userDisplayName(savedTrail.getUserProfile().getDisplayName())
                .userProfilePictureUrl(savedTrail.getUserProfile().getProfilePictureUrl())
                .trailId(savedTrail.getTrailId())
                .notes(savedTrail.getNotes())
                .savedAt(savedTrail.getSavedAt())
                .isActive(savedTrail.getIsActive());

        // Nota: Detalhes da trilha não estão mais disponíveis do trail-service
        // Os campos trailName, trailDescription, etc. ficarão null
        // Pode ser implementado um cache local ou busca via API REST se necessário

        return builder.build();
    }
}