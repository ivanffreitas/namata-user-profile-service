package com.namata.userprofile.controller;

import com.namata.userprofile.entity.UserProfile;
import com.namata.userprofile.entity.Statistics;
import com.namata.userprofile.repository.UserProfileRepository;
import com.namata.userprofile.repository.StatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/test")
public class TestDataController {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private StatisticsRepository statisticsRepository;

    @PostMapping("/insert-test-data")
    public ResponseEntity<Map<String, Object>> insertTestData() {
        try {
            System.out.println("=== DEBUG: Criando usuário de teste ===");
            
            // Teste sem builder - usando construtor padrão
        UserProfile user = new UserProfile();
        user.setUserId(UUID.randomUUID());
        user.setDisplayName("Ana Silva");
        user.setExperienceLevel(UserProfile.ExperienceLevel.BEGINNER);
        user.setLocation("São Paulo, SP");
        user.setProfilePictureUrl("https://example.com/avatar1.jpg");
        user.setIsActive(true);
        user.setIsVerified(false);
        user.setPrivacyLevel(UserProfile.PrivacyLevel.PUBLIC);
            
            System.out.println("=== DEBUG: Antes de salvar - userId: " + user.getUserId() + ", displayName: " + user.getDisplayName() + ", experienceLevel: " + user.getExperienceLevel() + " ===");
            
            UserProfile savedUser = userProfileRepository.save(user);
            
            System.out.println("=== DEBUG: Após salvar - userId: " + savedUser.getUserId() + ", id: " + savedUser.getId() + " ===");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuário de teste inserido com sucesso!");
            response.put("userId", savedUser.getUserId());
            response.put("id", savedUser.getId());
            
            // Criar estatísticas para o usuário de teste
            Statistics stats = new Statistics();
            stats.setUserProfile(savedUser);
            stats.setTotalPoints(8500);
            stats.setTotalTrailsCompleted(45);
            stats.setTotalDistanceKm(320.5);
            stats.setTotalElevationGainM(12500.0);
            stats.setTotalBadgesEarned(8);
            stats.setGlobalRank(1);
            stats.setLocalRank(1);
            stats.setCurrentStreak(15);
            stats.setLongestStreak(25);
            stats.setTotalTimeMinutes(850);
            stats.setHighestElevationM(2800);
            stats.setLongestTrailKm(85);
            stats.setTotalPhotosShared(320);
            stats.setTotalLikesReceived(1250);
            stats.setTotalCommentsReceived(180);
            stats.setTotalFollowers(450);
            stats.setTotalFollowing(320);
            stats.setTotalReviewsPosted(85);
            stats.setTotalGuidesBooked(12);
            stats.setLastActivityAt(LocalDateTime.now().minusDays(1));
            stats.setCreatedAt(LocalDateTime.now());
            stats.setUpdatedAt(LocalDateTime.now());
            
            Statistics savedStats = statisticsRepository.save(stats);
            
            response.put("statisticsId", savedStats.getId());
            
            return ResponseEntity.ok(response);
            
            // Dados de teste para usuários (comentado temporariamente)
            /*String[] names = {
                "Ana Silva", "Carlos Santos", "Maria Oliveira", "João Pereira", "Fernanda Costa",
                "Pedro Lima", "Juliana Rocha", "Roberto Alves", "Camila Ferreira", "Lucas Martins",
                "Beatriz Souza", "Rafael Dias", "Larissa Gomes", "Thiago Ribeiro", "Amanda Castro",
                "Diego Barbosa", "Gabriela Mendes", "Mateus Cardoso", "Isabela Nunes", "Gustavo Teixeira",
                "Natália Moreira", "Bruno Correia", "Vanessa Pinto", "André Vieira", "Priscila Araújo",
                "Felipe Ramos", "Carla Freitas", "Rodrigo Silva", "Aline Santos", "Marcelo Costa",
                "Renata Lima", "Fábio Rocha", "Patrícia Alves", "Leonardo Ferreira", "Cristina Martins",
                "Vinicius Souza", "Tatiana Dias", "Henrique Gomes", "Simone Ribeiro", "Alexandre Castro",
                "Mônica Barbosa", "Danilo Mendes", "Eliane Cardoso", "Ricardo Nunes", "Luciana Teixeira",
                "Sérgio Moreira", "Adriana Correia", "Márcio Pinto", "Cláudia Vieira", "Edson Araújo",
                "Silvia Ramos", "Paulo Freitas", "Rosana Silva", "Júlio Santos", "Vera Costa",
                "Antônio Lima", "Lúcia Rocha", "José Alves", "Helena Ferreira", "Rogério Martins",
                "Célia Souza", "Marcos Dias", "Denise Gomes", "Flávio Ribeiro", "Marta Castro",
                "Luiz Barbosa", "Sandra Mendes", "Gilberto Cardoso", "Neusa Nunes", "Wilson Teixeira",
                "Ivone Moreira", "Geraldo Correia", "Solange Pinto", "Valter Vieira", "Glória Araújo",
                "Osvaldo Ramos", "Terezinha Freitas", "Sebastião Silva", "Aparecida Santos", "Francisco Costa",
                "Conceição Lima", "Raimundo Rocha", "Francisca Alves", "Edivaldo Ferreira", "Raimunda Martins",
                "Domingos Souza", "Antônia Dias", "Manoel Gomes", "Josefa Ribeiro", "Benedito Castro",
                "Iracema Barbosa", "Severino Mendes", "Zilda Cardoso", "Joaquim Nunes", "Dalva Teixeira",
                "Expedito Moreira", "Creusa Correia", "Lindomar Pinto", "Odete Vieira", "Valdeci Araújo"
            };*/

            /*
            // Código comentado temporariamente para teste simples
            String[] explorerNames = {
                "Trilheira Ana", "Montanhista Carlos", "Aventureira Maria", "Caminhante João", "Exploradora Fernanda",
                // ... resto dos nomes
            };

            UserProfile.ExperienceLevel[] levels = {UserProfile.ExperienceLevel.EXPERT, UserProfile.ExperienceLevel.ADVANCED, UserProfile.ExperienceLevel.INTERMEDIATE, UserProfile.ExperienceLevel.BEGINNER};
            String[] locations = {
                "São Paulo, SP", "Rio de Janeiro, RJ", "Belo Horizonte, MG", "Curitiba, PR", "Porto Alegre, RS",
                // ... resto das localizações
            };

            List<UserProfile> userProfiles = new ArrayList<>();
            List<Statistics> statistics = new ArrayList<>();

            Random random = new Random();

            for (int i = 0; i < 100; i++) {
                // Criar perfil de usuário usando builder
                UserProfile userProfile = UserProfile.builder()
                    .userId(UUID.randomUUID())
                    .displayName(names[i])
                    .experienceLevel(levels[random.nextInt(levels.length)])
                    .location(locations[random.nextInt(locations.length)])
                    .profilePictureUrl("https://example.com/avatar" + (i + 1) + ".jpg")
                    .isActive(true)
                    .isVerified(false)
                    .privacyLevel(UserProfile.PrivacyLevel.PUBLIC)
                    .build();
                
                userProfiles.add(userProfile);
            }

            // Salvar perfis de usuários primeiro
            List<UserProfile> savedUserProfiles = userProfileRepository.saveAll(userProfiles);

            // Criar estatísticas para cada usuário
            for (int i = 0; i < savedUserProfiles.size(); i++) {
                UserProfile userProfile = savedUserProfiles.get(i);
                Statistics stat = new Statistics();
                stat.setUserProfile(userProfile);
                
                // Gerar dados variados baseados na posição para criar um ranking interessante
                int basePoints = 15000 - (i * 150) + random.nextInt(100);
                int trails = 150 - (i * 1) + random.nextInt(10);
                double distance = 2500.0 - (i * 25.0) + random.nextDouble() * 10;
                int elevation = 45000 - (i * 450) + random.nextInt(500);
                int badges = Math.max(0, 25 - (i / 4));
                
                stat.setTotalPoints(Math.max(100, basePoints));
                stat.setTotalTrailsCompleted(Math.max(1, trails));
                stat.setTotalDistanceKm(Math.max(5.0, distance));
                stat.setTotalElevationGainM(Math.max(10.0, (double) elevation));
                stat.setTotalBadgesEarned(badges);
                stat.setGlobalRank(i + 1);
                stat.setLocalRank(i + 1);
                stat.setCreatedAt(LocalDateTime.now());
                stat.setUpdatedAt(LocalDateTime.now());
                
                statistics.add(stat);
            }

            // Salvar estatísticas
            statisticsRepository.saveAll(statistics);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "100 usuários e estatísticas inseridos com sucesso!");
            response.put("userProfilesCreated", savedUserProfiles.size());
            response.put("statisticsCreated", statistics.size());

            return ResponseEntity.ok(response);
            */

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erro ao inserir dados de teste: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/insert-single-ranking-user")
    public ResponseEntity<Map<String, Object>> insertSingleRankingUser() {
        try {
            System.out.println("=== DEBUG: Inserindo um único usuário de ranking ===");
            
            // Criar um único usuário de teste
            UserProfile userProfile = new UserProfile();
            userProfile.setUserId(UUID.randomUUID());
            userProfile.setDisplayName("Ana Silva");
            userProfile.setExperienceLevel(UserProfile.ExperienceLevel.EXPERT);
            userProfile.setLocation("São Paulo, SP");
            userProfile.setProfilePictureUrl("https://example.com/avatar101.jpg");
            userProfile.setIsActive(true);
            userProfile.setIsVerified(true);
            userProfile.setPrivacyLevel(UserProfile.PrivacyLevel.PUBLIC);
            userProfile.setBio("Explorador apaixonado por trilhas e natureza");
            
            System.out.println("=== DEBUG: Usuário criado - userId: " + userProfile.getUserId() + ", displayName: " + userProfile.getDisplayName() + " ====");
            
            // Salvar o perfil
            UserProfile savedProfile = userProfileRepository.save(userProfile);
            System.out.println("=== DEBUG: Perfil salvo - id: " + savedProfile.getId() + ", userId: " + savedProfile.getUserId() + " ====");
            
            // Criar estatísticas
            com.namata.userprofile.entity.Statistics stat = new com.namata.userprofile.entity.Statistics();
            stat.setUserProfile(savedProfile);
            stat.setTotalPoints(8500);
            stat.setTotalTrailsCompleted(125);
            stat.setTotalDistanceKm(3500.5);
            stat.setTotalElevationGainM(85000.0);
            stat.setTotalBadgesEarned(45);
            stat.setGlobalRank(1);
            stat.setLocalRank(1);
            stat.setCurrentStreak(15);
            stat.setLongestStreak(25);
            stat.setLastActivityAt(LocalDateTime.now().minusDays(1));
            
            com.namata.userprofile.entity.Statistics savedStat = statisticsRepository.save(stat);
            System.out.println("=== DEBUG: Estatística salva - id: " + savedStat.getId() + " ====");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuário de ranking inserido com sucesso!");
            response.put("userId", savedProfile.getUserId());
            response.put("profileId", savedProfile.getId());
            response.put("statisticsId", savedStat.getId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.out.println("=== DEBUG: Erro ao inserir usuário de ranking: " + e.getMessage() + " ====");
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erro ao inserir usuário de ranking: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @PostMapping("/insert-ranking-data")
    public ResponseEntity<Map<String, Object>> insertRankingData() {
        try {
            System.out.println("=== DEBUG: Inserindo dados de ranking ===");
            
            // Dados para ranking - nomes e informações
            String[] names = {
                "Carlos Aventureiro", "Ana Montanhista", "Pedro Trilheiro", "Maria Natureza", "João Escalador",
                "Fernanda Caminhante", "Roberto Explorador", "Juliana Verde", "Lucas Aventura", "Camila Montanha",
                "Diego Silva", "Beatriz Costa", "Rafael Outdoor", "Larissa Trilha", "Thiago Natureza"
            };
            
            String[] locations = {
                "Rio de Janeiro, RJ", "Belo Horizonte, MG", "São Paulo, SP", "Curitiba, PR", "Florianópolis, SC",
                "Porto Alegre, RS", "Salvador, BA", "Brasília, DF", "Recife, PE", "Manaus, AM",
                "Fortaleza, CE", "Goiânia, GO", "Vitória, ES", "Campo Grande, MS", "João Pessoa, PB"
            };
            
            UserProfile.ExperienceLevel[] levels = {
                UserProfile.ExperienceLevel.EXPERT, UserProfile.ExperienceLevel.EXPERT, UserProfile.ExperienceLevel.ADVANCED, 
                UserProfile.ExperienceLevel.ADVANCED, UserProfile.ExperienceLevel.EXPERT, UserProfile.ExperienceLevel.INTERMEDIATE,
                UserProfile.ExperienceLevel.ADVANCED, UserProfile.ExperienceLevel.INTERMEDIATE, UserProfile.ExperienceLevel.BEGINNER,
                UserProfile.ExperienceLevel.ADVANCED, UserProfile.ExperienceLevel.INTERMEDIATE, UserProfile.ExperienceLevel.BEGINNER,
                UserProfile.ExperienceLevel.EXPERT, UserProfile.ExperienceLevel.INTERMEDIATE, UserProfile.ExperienceLevel.ADVANCED
            };
            
            // Dados de pontuação para ranking (decrescente)
            int[] points = {25000, 23500, 22000, 20500, 19000, 17500, 16000, 14500, 13000, 11500, 10000, 8500, 7000, 5500, 4000};
            int[] trails = {180, 165, 150, 140, 130, 120, 110, 100, 90, 80, 70, 60, 50, 40, 30};
            double[] distances = {3500.5, 3200.8, 2900.3, 2650.7, 2400.2, 2150.9, 1900.5, 1650.8, 1400.3, 1150.7, 900.2, 750.5, 600.8, 450.3, 300.7};
            int[] elevations = {85000, 78000, 72000, 65000, 58000, 52000, 45000, 38000, 32000, 25000, 20000, 15000, 12000, 8000, 5000};
            int[] badges = {45, 42, 38, 35, 32, 28, 25, 22, 18, 15, 12, 10, 8, 6, 4};
            
            List<UserProfile> userProfiles = new ArrayList<>();
            List<com.namata.userprofile.entity.Statistics> statistics = new ArrayList<>();
            
            // Criar perfis de usuários
            for (int i = 0; i < names.length; i++) {
                UserProfile userProfile = new UserProfile();
                UUID userId = UUID.randomUUID(); // IDs aleatórios
                
                System.out.println("=== DEBUG: Criando usuário " + i + " com userId: " + userId + " ====");
                
                userProfile.setUserId(userId);
                userProfile.setDisplayName(names[i]);
                userProfile.setExperienceLevel(levels[i]);
                userProfile.setLocation(locations[i]);
                userProfile.setProfilePictureUrl("https://example.com/avatar" + (101 + i) + ".jpg");
                userProfile.setIsActive(true);
                userProfile.setIsVerified(i < 10); // Primeiros 10 verificados
                userProfile.setPrivacyLevel(UserProfile.PrivacyLevel.PUBLIC);
                userProfile.setBio("Explorador apaixonado por trilhas e natureza");
                
                System.out.println("=== DEBUG: Usuário criado - userId: " + userProfile.getUserId() + ", displayName: " + userProfile.getDisplayName() + " ====");
                
                userProfiles.add(userProfile);
            }
            
            // Salvar perfis primeiro
            System.out.println("=== DEBUG: Tentando salvar " + userProfiles.size() + " perfis de usuário ===");
            
            List<UserProfile> savedUserProfiles = new ArrayList<>();
            for (UserProfile profile : userProfiles) {
                try {
                    System.out.println("=== DEBUG: Salvando perfil - userId: " + profile.getUserId() + ", displayName: " + profile.getDisplayName() + " ====");
                    UserProfile saved = userProfileRepository.save(profile);
                    savedUserProfiles.add(saved);
                    System.out.println("=== DEBUG: Perfil salvo com sucesso - id: " + saved.getId() + ", userId: " + saved.getUserId() + " ====");
                } catch (Exception e) {
                    System.out.println("=== DEBUG: Erro ao salvar perfil - userId: " + profile.getUserId() + ", erro: " + e.getMessage() + " ====");
                    throw e;
                }
            }
            
            System.out.println("=== DEBUG: Salvos " + savedUserProfiles.size() + " perfis de usuário ===");
            
            // Criar estatísticas para cada usuário
            for (int i = 0; i < savedUserProfiles.size(); i++) {
                UserProfile userProfile = savedUserProfiles.get(i);
                com.namata.userprofile.entity.Statistics stat = new com.namata.userprofile.entity.Statistics();
                stat.setUserProfile(userProfile);
                
                stat.setTotalPoints(points[i]);
                stat.setTotalTrailsCompleted(trails[i]);
                stat.setTotalDistanceKm(distances[i]);
                stat.setTotalElevationGainM((double) elevations[i]);
                stat.setTotalBadgesEarned(badges[i]);
                stat.setGlobalRank(i + 1);
                stat.setLocalRank(i + 1);
                stat.setCurrentStreak(Math.max(0, 15 - i));
                stat.setLongestStreak(Math.max(1, 25 - i));
                stat.setTotalTimeMinutes(points[i] / 10); // Aproximação
                stat.setHighestElevationM((2800 - (i * 200)));
                stat.setLongestTrailKm((int)(85.2 - (i * 5)));
                stat.setTotalPhotosShared(320 - (i * 20));
                stat.setTotalLikesReceived(1250 - (i * 80));
                stat.setTotalCommentsReceived(180 - (i * 10));
                stat.setTotalFollowers(450 - (i * 25));
                stat.setTotalFollowing(320 - (i * 15));
                stat.setTotalReviewsPosted(85 - (i * 5));
                stat.setTotalGuidesBooked(Math.max(0, 12 - i));
                stat.setLastActivityAt(LocalDateTime.now().minusDays(i + 1));
                stat.setCreatedAt(LocalDateTime.now());
                stat.setUpdatedAt(LocalDateTime.now());
                
                statistics.add(stat);
            }
            
            // Salvar estatísticas
            statisticsRepository.saveAll(statistics);
            System.out.println("=== DEBUG: Salvas " + statistics.size() + " estatísticas ===");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Dados de ranking inseridos com sucesso!");
            response.put("userProfilesCreated", savedUserProfiles.size());
            response.put("statisticsCreated", statistics.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.out.println("=== DEBUG: Erro ao inserir dados: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erro ao inserir dados de ranking: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/clear-test-data")
    public ResponseEntity<Map<String, Object>> clearTestData() {
        try {
            statisticsRepository.deleteAll();
            userProfileRepository.deleteAll();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Todos os dados de teste foram removidos!");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erro ao limpar dados de teste: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}