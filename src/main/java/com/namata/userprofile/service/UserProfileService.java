package com.namata.userprofile.service;

import com.namata.userprofile.client.AuthServiceClient;
import com.namata.userprofile.dto.CreateUserProfileRequest;
import com.namata.userprofile.dto.UpdateUserProfileRequest;
import com.namata.userprofile.dto.UserProfileDTO;
import com.namata.userprofile.entity.Statistics;
import com.namata.userprofile.entity.UserProfile;
import com.namata.userprofile.repository.StatisticsRepository;
import com.namata.userprofile.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final StatisticsRepository statisticsRepository;
    private final AuthServiceClient authServiceClient;
    
    @Value("${app.upload.profile-pictures.directory}")
    private String uploadDir;

    public UserProfileDTO createProfile(CreateUserProfileRequest request) {
        log.info("Criando perfil para usuário ID: {}", request.getUserId());

        // Verificar se já existe perfil para este usuário
        if (userProfileRepository.existsByUserId(request.getUserId())) {
            throw new IllegalArgumentException("Perfil já existe para este usuário");
        }

        UserProfile profile = UserProfile.builder()
                .userId(request.getUserId())
                .displayName(request.getDisplayName())
                .bio(request.getBio())
                .profilePictureUrl(request.getProfilePictureUrl())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .location(request.getLocation())
                .phoneNumber(request.getPhoneNumber())
                .experienceLevel(request.getExperienceLevel() != null ? 
                    request.getExperienceLevel() : UserProfile.ExperienceLevel.BEGINNER)
                .interests(request.getInterests())
                .explorationType(request.getExplorationType())
                .privacyLevel(request.getPrivacyLevel() != null ? 
                    request.getPrivacyLevel() : UserProfile.PrivacyLevel.PUBLIC)
                .isActive(true)
                .isVerified(false)
                .build();

        UserProfile savedProfile = userProfileRepository.save(profile);

        // Criar estatísticas iniciais
        Statistics statistics = Statistics.builder()
                .userProfile(savedProfile)
                .build();
        statisticsRepository.save(statistics);

        log.info("Perfil criado com sucesso para usuário ID: {}", request.getUserId());
        return convertToDTO(savedProfile);
    }

    public Optional<UserProfileDTO> getProfileByUserId(UUID userId) {
        return userProfileRepository.findByUserId(userId)
                .map(this::convertToDTO);
    }

    public Optional<UserProfileDTO> getProfileById(UUID profileId) {
        return userProfileRepository.findById(profileId)
                .map(this::convertToDTO);
    }

    public UserProfileDTO updateProfile(UUID userId, UpdateUserProfileRequest request) {
        log.info("Atualizando perfil para usuário ID: {}", userId);

        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil não encontrado"));

        // Atualizar apenas campos não nulos
        if (request.getDisplayName() != null) {
            profile.setDisplayName(request.getDisplayName());
        }
        if (request.getBio() != null) {
            profile.setBio(request.getBio());
        }
        if (request.getProfilePictureUrl() != null) {
            profile.setProfilePictureUrl(request.getProfilePictureUrl());
        }
        if (request.getDateOfBirth() != null) {
            profile.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getGender() != null) {
            profile.setGender(request.getGender());
        }
        if (request.getLocation() != null) {
            profile.setLocation(request.getLocation());
        }
        if (request.getPhoneNumber() != null) {
            profile.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getExperienceLevel() != null) {
            profile.setExperienceLevel(request.getExperienceLevel());
        }
        if (request.getInterests() != null) {
            profile.setInterests(request.getInterests());
        }
        if (request.getExplorationType() != null) {
            profile.setExplorationType(request.getExplorationType());
        }
        if (request.getPrivacyLevel() != null) {
            profile.setPrivacyLevel(request.getPrivacyLevel());
        }

        UserProfile updatedProfile = userProfileRepository.save(profile);
        log.info("Perfil atualizado com sucesso para usuário ID: {}", userId);
        
        return convertToDTO(updatedProfile);
    }

    public void deactivateProfile(UUID userId) {
        log.info("Desativando perfil para usuário ID: {}", userId);

        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil não encontrado"));

        profile.setIsActive(false);
        userProfileRepository.save(profile);

        log.info("Perfil desativado com sucesso para usuário ID: {}", userId);
    }

    public void verifyProfile(UUID userId) {
        log.info("Verificando perfil para usuário ID: {}", userId);

        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil não encontrado"));

        profile.setIsVerified(true);
        userProfileRepository.save(profile);

        log.info("Perfil verificado com sucesso para usuário ID: {}", userId);
    }

    @Transactional(readOnly = true)
    public List<UserProfileDTO> getActiveProfiles() {
        return userProfileRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToDTOWithoutStats)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public long countActiveProfiles() {
        return userProfileRepository.countActiveProfiles();
    }

    @Transactional(readOnly = true)
    public Page<UserProfileDTO> searchProfiles(String displayName, String location, 
                                              UserProfile.ExperienceLevel experienceLevel, 
                                              Pageable pageable) {
        return userProfileRepository.findProfilesWithFilters(displayName, location, experienceLevel, pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public List<UserProfileDTO> getProfilesByLocation(String location) {
        return userProfileRepository.findByLocationContainingAndIsActiveTrue(location)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserProfileDTO> getProfilesByExperienceLevel(UserProfile.ExperienceLevel level) {
        return userProfileRepository.findByExperienceLevelAndIsActiveTrue(level)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public Page<UserProfileDTO> getRankingByPoints(Pageable pageable) {
        log.info("Buscando ranking por pontos");
        
        // Buscar todos os perfis com suas estatísticas, ordenados por pontos
        List<UserProfile> profiles = userProfileRepository.findAll();
        
        // Converter para DTO e ordenar por pontos (decrescente)
        List<UserProfileDTO> profileDTOs = profiles.stream()
                .map(this::convertToDTO)
                .sorted((a, b) -> Integer.compare(b.getTotalPoints(), a.getTotalPoints()))
                .toList();
        
        // Converter para Page
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), profileDTOs.size());
        
        List<UserProfileDTO> pageContent = profileDTOs.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, profileDTOs.size());
    }
    
    public Page<UserProfileDTO> getRankingByTrails(Pageable pageable) {
        log.info("Buscando ranking por trilhas");
        
        // Buscar todos os perfis com suas estatísticas, ordenados por trilhas
        List<UserProfile> profiles = userProfileRepository.findAll();
        
        // Converter para DTO e ordenar por trilhas (decrescente)
        List<UserProfileDTO> profileDTOs = profiles.stream()
                .map(this::convertToDTO)
                .sorted((a, b) -> Integer.compare(b.getTotalTrailsCompleted(), a.getTotalTrailsCompleted()))
                .toList();
        
        // Converter para Page
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), profileDTOs.size());
        
        List<UserProfileDTO> pageContent = profileDTOs.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, profileDTOs.size());
    }

    public UserProfileDTO updateProfilePicture(UUID userId, MultipartFile file) {
        log.info("Atualizando foto de perfil para usuário ID: {}", userId);
        
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Perfil não encontrado para o usuário: " + userId));
        
        try {
            // Criar diretório se não existir
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Gerar nome único para o arquivo
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String filename = userId + "_" + System.currentTimeMillis() + fileExtension;
            
            // Salvar arquivo
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Atualizar URL no perfil
            String profilePictureUrl = "/uploads/profile-pictures/" + filename;
            profile.setProfilePictureUrl(profilePictureUrl);
            
            UserProfile savedProfile = userProfileRepository.save(profile);
            log.info("Foto de perfil atualizada com sucesso para usuário ID: {}", userId);
            
            return convertToDTO(savedProfile);
            
        } catch (IOException e) {
            log.error("Erro ao salvar arquivo de imagem: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao salvar imagem: " + e.getMessage());
        }
    }

    private UserProfileDTO convertToDTO(UserProfile profile) {
        // Buscar estatísticas básicas
        Statistics stats = statisticsRepository.findByUserProfile(profile).orElse(null);
        
        // Buscar firstName do auth-service
        String firstName = null;
        try {
            Map<String, Object> userData = authServiceClient.getUserById(profile.getUserId());
            firstName = (String) userData.get("firstName");
        } catch (Exception e) {
            log.warn("Erro ao buscar dados do usuário do auth-service: {}", e.getMessage());
        }

        return UserProfileDTO.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .firstName(firstName)
                .displayName(profile.getDisplayName())
                .bio(profile.getBio())
                .profilePictureUrl(profile.getProfilePictureUrl())
                .dateOfBirth(profile.getDateOfBirth())
                .gender(profile.getGender())
                .location(profile.getLocation())
                .phoneNumber(profile.getPhoneNumber())
                .experienceLevel(profile.getExperienceLevel())
                .interests(null) // Interests são ignorados na serialização
                .explorationType(profile.getExplorationType())
                .privacyLevel(profile.getPrivacyLevel())
                .isActive(profile.getIsActive())
                .isVerified(profile.getIsVerified())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .totalTrailsCompleted(stats != null ? stats.getTotalTrailsCompleted() : 0)
                .totalBadgesEarned(stats != null ? stats.getTotalBadgesEarned() : 0)
                .totalPoints(stats != null ? stats.getTotalPoints() : 0)
                .build();
    }
    
    private UserProfileDTO convertToDTOWithoutStats(UserProfile profile) {
        return UserProfileDTO.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .displayName(profile.getDisplayName())
                .bio(profile.getBio())
                .profilePictureUrl(profile.getProfilePictureUrl())
                .dateOfBirth(profile.getDateOfBirth())
                .gender(profile.getGender())
                .location(profile.getLocation())
                .phoneNumber(profile.getPhoneNumber())
                .experienceLevel(profile.getExperienceLevel())
                .interests(null) // Interests são ignorados na serialização
                .explorationType(profile.getExplorationType())
                .privacyLevel(profile.getPrivacyLevel())
                .isActive(profile.getIsActive())
                .isVerified(profile.getIsVerified())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .totalTrailsCompleted(0)
                .totalBadgesEarned(0)
                .totalPoints(0)
                .build();
    }
}