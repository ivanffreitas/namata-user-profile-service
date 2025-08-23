# Script PowerShell para inserir dados de teste no PostgreSQL
param(
    [string]$Server = "localhost",
    [int]$Port = 5433,
    [string]$Database = "namata_profile",
    [string]$Username = "postgres",
    [string]$Password = "admin"
)

Write-Host "Testando conectividade com PostgreSQL..." -ForegroundColor Yellow
Write-Host "Server: $Server" -ForegroundColor Gray
Write-Host "Port: $Port" -ForegroundColor Gray
Write-Host "Database: $Database" -ForegroundColor Gray

# Funcao para testar conexao
function Test-PostgreSQLConnection {
    param(
        [string]$Server,
        [int]$Port
    )
    
    try {
        $tcpClient = New-Object System.Net.Sockets.TcpClient
        $tcpClient.Connect($Server, $Port)
        $connected = $tcpClient.Connected
        $tcpClient.Close()
        return $connected
    }
    catch {
        return $false
    }
}

# Testar conexao
if (-not (Test-PostgreSQLConnection -Server $Server -Port $Port)) {
    Write-Host "Nao foi possivel conectar ao PostgreSQL na porta $Port" -ForegroundColor Red
    
    # Tentar porta 5432
    Write-Host "Tentando porta 5432..." -ForegroundColor Yellow
    if (Test-PostgreSQLConnection -Server $Server -Port 5432) {
        Write-Host "PostgreSQL encontrado na porta 5432" -ForegroundColor Green
        $Port = 5432
    } else {
        Write-Host "PostgreSQL nao encontrado nas portas 5432 e 5433" -ForegroundColor Red
        Write-Host "Verifique se o PostgreSQL esta rodando" -ForegroundColor Yellow
    }
} else {
    Write-Host "PostgreSQL esta acessivel na porta $Port" -ForegroundColor Green
}

# Criar arquivo SQL
$sqlFile = "manual_insert_data.sql"
Write-Host "Criando arquivo SQL: $sqlFile" -ForegroundColor Green

# Conteudo SQL
$sqlContent = @'
-- Script SQL para inserir dados de teste no PostgreSQL
-- Execute este script no seu cliente PostgreSQL

-- Criar tabelas se nao existirem
CREATE TABLE IF NOT EXISTS user_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    display_name VARCHAR(100),
    bio VARCHAR(500),
    profile_picture_url TEXT,
    date_of_birth DATE,
    gender VARCHAR(20),
    location TEXT,
    phone_number VARCHAR(20),
    experience_level VARCHAR(20) DEFAULT 'BEGINNER',
    exploration_type VARCHAR(30),
    privacy_level VARCHAR(20) DEFAULT 'PUBLIC',
    is_active BOOLEAN DEFAULT TRUE,
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_profile_interests (
    user_profile_id BIGINT NOT NULL,
    interest VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_profile_id) REFERENCES user_profiles(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS statistics (
    id BIGSERIAL PRIMARY KEY,
    user_profile_id BIGINT NOT NULL UNIQUE,
    total_trails_completed INTEGER DEFAULT 0,
    total_distance_km DECIMAL(10,2) DEFAULT 0.0,
    total_time_minutes INTEGER DEFAULT 0,
    total_elevation_gain_m DECIMAL(10,2) DEFAULT 0.0,
    longest_trail_km INTEGER DEFAULT 0,
    highest_elevation_m INTEGER DEFAULT 0,
    total_photos_shared INTEGER DEFAULT 0,
    total_reviews_posted INTEGER DEFAULT 0,
    total_likes_received INTEGER DEFAULT 0,
    total_comments_received INTEGER DEFAULT 0,
    total_badges_earned INTEGER DEFAULT 0,
    total_points INTEGER DEFAULT 0,
    current_streak INTEGER DEFAULT 0,
    longest_streak INTEGER DEFAULT 0,
    total_followers INTEGER DEFAULT 0,
    total_following INTEGER DEFAULT 0,
    total_guides_booked INTEGER DEFAULT 0,
    global_rank INTEGER DEFAULT 0,
    local_rank INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_activity_at TIMESTAMP,
    FOREIGN KEY (user_profile_id) REFERENCES user_profiles(id) ON DELETE CASCADE
);

-- Inserir usuarios de teste
INSERT INTO user_profiles (
    user_id, display_name, bio, profile_picture_url, date_of_birth, gender, 
    location, phone_number, experience_level, exploration_type, privacy_level, 
    is_active, is_verified
) VALUES 
(101, 'Carlos Aventureiro', 'Explorador experiente das trilhas cariocas', 'https://example.com/avatar101.jpg', '1985-03-15', 'MALE', 'Rio de Janeiro, RJ', '+5521987654321', 'EXPERT', 'ADVENTURE', 'PUBLIC', true, true),
(102, 'Ana Montanhista', 'Apaixonada por montanhas e natureza', 'https://example.com/avatar102.jpg', '1990-07-22', 'FEMALE', 'Belo Horizonte, MG', '+5531987654322', 'EXPERT', 'HIKING', 'PUBLIC', true, true),
(103, 'Pedro Trilheiro', 'Descobrindo novas trilhas em SP', 'https://example.com/avatar103.jpg', '1988-11-08', 'MALE', 'São Paulo, SP', '+5511987654323', 'ADVANCED', 'HIKING', 'PUBLIC', true, false),
(104, 'Maria Natureza', 'Observadora da fauna e flora', 'https://example.com/avatar104.jpg', '1992-05-30', 'FEMALE', 'Curitiba, PR', '+5541987654324', 'ADVANCED', 'NATURE_OBSERVATION', 'PUBLIC', true, true),
(105, 'João Explorador', 'Aventuras em busca do desconhecido', 'https://example.com/avatar105.jpg', '1987-09-12', 'MALE', 'Porto Alegre, RS', '+5551987654325', 'EXPERT', 'ADVENTURE', 'PUBLIC', true, false),
(106, 'Fernanda Caminhante', 'Trilhas são minha paixão', 'https://example.com/avatar106.jpg', '1991-12-03', 'FEMALE', 'Salvador, BA', '+5571987654326', 'INTERMEDIATE', 'HIKING', 'PUBLIC', true, false),
(107, 'Roberto Montanha', 'Conquistando picos e montanhas', 'https://example.com/avatar107.jpg', '1984-06-18', 'MALE', 'Fortaleza, CE', '+5585987654327', 'ADVANCED', 'HIKING', 'PUBLIC', true, true),
(108, 'Camila Rocha', 'Fotógrafa de natureza', 'https://example.com/avatar108.jpg', '1993-04-25', 'FEMALE', 'Brasília, DF', '+5561987654328', 'EXPERT', 'PHOTOGRAPHY', 'PUBLIC', true, false),
(109, 'Lucas Verde', 'Ecoturismo e sustentabilidade', 'https://example.com/avatar109.jpg', '1989-08-07', 'MALE', 'Recife, PE', '+5581987654329', 'INTERMEDIATE', 'RESEARCH', 'PUBLIC', true, false),
(110, 'Juliana Trilha', 'Sempre em busca de novas aventuras', 'https://example.com/avatar110.jpg', '1986-01-14', 'FEMALE', 'Manaus, AM', '+5592987654330', 'ADVANCED', 'ADVENTURE', 'PUBLIC', true, true),
(111, 'Diego Aventura', 'Trilheiro de fim de semana', 'https://example.com/avatar111.jpg', '1994-10-29', 'MALE', 'Goiânia, GO', '+5562987654331', 'INTERMEDIATE', 'HIKING', 'PUBLIC', true, false),
(112, 'Patrícia Natureza', 'Conectada com a natureza', 'https://example.com/avatar112.jpg', '1988-03-16', 'FEMALE', 'Florianópolis, SC', '+5548987654332', 'ADVANCED', 'RELAXATION', 'PUBLIC', true, true),
(113, 'Rafael Pico', 'Escalador e montanhista', 'https://example.com/avatar113.jpg', '1983-07-21', 'MALE', 'Vitória, ES', '+5527987654333', 'EXPERT', 'ADVENTURE', 'PUBLIC', true, false),
(114, 'Carla Caminho', 'Trilhas urbanas e rurais', 'https://example.com/avatar114.jpg', '1995-11-05', 'FEMALE', 'Campo Grande, MS', '+5567987654334', 'BEGINNER', 'HIKING', 'PUBLIC', true, false),
(115, 'Thiago Mata', 'Preservação e aventura', 'https://example.com/avatar115.jpg', '1990-02-28', 'MALE', 'Belém, PA', '+5591987654335', 'INTERMEDIATE', 'RESEARCH', 'PUBLIC', true, true)
ON CONFLICT (user_id) DO NOTHING;

-- Inserir estatisticas para ranking (ordenado por pontos decrescentes)
INSERT INTO statistics (
    user_profile_id, total_trails_completed, total_distance_km, total_time_minutes, 
    total_elevation_gain_m, longest_trail_km, highest_elevation_m, total_photos_shared, 
    total_reviews_posted, total_likes_received, total_comments_received, 
    total_badges_earned, total_points, current_streak, longest_streak, 
    total_followers, total_following, total_guides_booked, global_rank, local_rank,
    last_activity_at
) 
SELECT 
    up.id,
    stats.total_trails_completed, stats.total_distance_km, stats.total_time_minutes,
    stats.total_elevation_gain_m, stats.longest_trail_km, stats.highest_elevation_m,
    stats.total_photos_shared, stats.total_reviews_posted, stats.total_likes_received,
    stats.total_comments_received, stats.total_badges_earned, stats.total_points,
    stats.current_streak, stats.longest_streak, stats.total_followers,
    stats.total_following, stats.total_guides_booked, stats.global_rank, stats.local_rank,
    CURRENT_TIMESTAMP
FROM user_profiles up
JOIN (
    VALUES 
    (101, 180, 3500.50, 21600, 85000.0, 45, 2800, 320, 85, 1250, 340, 45, 25000, 15, 28, 450, 280, 12, 1, 1),
    (102, 165, 3200.80, 19800, 78000.0, 42, 2650, 290, 78, 1180, 315, 42, 23500, 12, 25, 420, 260, 11, 2, 1),
    (103, 150, 2900.30, 18000, 72000.0, 38, 2500, 260, 72, 1100, 290, 38, 22000, 10, 22, 380, 240, 10, 3, 2),
    (104, 140, 2650.70, 16800, 65000.0, 35, 2350, 240, 65, 980, 265, 35, 20500, 8, 20, 350, 220, 9, 4, 2),
    (105, 130, 2400.20, 15600, 58000.0, 32, 2200, 220, 58, 920, 240, 32, 19000, 7, 18, 320, 200, 8, 5, 3),
    (106, 120, 2150.90, 14400, 52000.0, 30, 2050, 200, 52, 850, 220, 28, 17500, 6, 16, 290, 180, 7, 6, 3),
    (107, 110, 1900.50, 13200, 45000.0, 28, 1900, 180, 45, 780, 195, 25, 16000, 5, 14, 260, 160, 6, 7, 4),
    (108, 100, 1650.80, 12000, 38000.0, 25, 1750, 160, 38, 720, 170, 22, 14500, 4, 12, 230, 140, 5, 8, 4),
    (109, 90, 1400.30, 10800, 32000.0, 22, 1600, 140, 32, 650, 145, 18, 13000, 3, 10, 200, 120, 4, 9, 5),
    (110, 80, 1150.70, 9600, 25000.0, 20, 1450, 120, 25, 580, 120, 15, 11500, 2, 8, 170, 100, 3, 10, 5),
    (111, 70, 900.20, 8400, 20000.0, 18, 1300, 100, 20, 520, 95, 12, 10000, 1, 6, 140, 80, 2, 11, 6),
    (112, 60, 750.50, 7200, 15000.0, 15, 1150, 80, 15, 450, 70, 10, 8500, 0, 4, 110, 60, 1, 12, 6),
    (113, 50, 600.80, 6000, 12000.0, 12, 1000, 60, 12, 380, 45, 8, 7000, 0, 2, 80, 40, 0, 13, 7),
    (114, 40, 450.30, 4800, 8000.0, 10, 850, 40, 8, 320, 20, 6, 5500, 0, 1, 50, 20, 0, 14, 7),
    (115, 30, 300.70, 3600, 5000.0, 8, 700, 20, 5, 250, 10, 4, 4000, 0, 0, 20, 10, 0, 15, 8)
) AS stats(user_id, total_trails_completed, total_distance_km, total_time_minutes, total_elevation_gain_m, longest_trail_km, highest_elevation_m, total_photos_shared, total_reviews_posted, total_likes_received, total_comments_received, total_badges_earned, total_points, current_streak, longest_streak, total_followers, total_following, total_guides_booked, global_rank, local_rank)
ON up.user_id = stats.user_id
ON CONFLICT (user_profile_id) DO NOTHING;

-- Verificar dados inseridos
SELECT 'Usuarios criados:' as info, COUNT(*) as total FROM user_profiles;
SELECT 'Estatisticas criadas:' as info, COUNT(*) as total FROM statistics;

-- Ranking por pontos (top 10)
SELECT 
    up.display_name,
    up.location,
    up.experience_level,
    s.total_points,
    s.total_trails_completed,
    s.total_distance_km,
    s.global_rank
FROM user_profiles up
JOIN statistics s ON up.id = s.user_profile_id
WHERE up.is_active = true
ORDER BY s.total_points DESC
LIMIT 10;
'@

# Salvar arquivo SQL
$sqlContent | Out-File -FilePath $sqlFile -Encoding UTF8

Write-Host "" 
Write-Host "Arquivo SQL criado: $sqlFile" -ForegroundColor Green
Write-Host "" 
Write-Host "INSTRUCOES PARA INSERIR OS DADOS:" -ForegroundColor Cyan
Write-Host "1. Abra seu cliente PostgreSQL (pgAdmin, DBeaver, etc.)" -ForegroundColor White
Write-Host "2. Conecte-se ao banco de dados namata_profile" -ForegroundColor White
Write-Host "3. Execute o arquivo SQL: $sqlFile" -ForegroundColor White
Write-Host "4. Verifique se os dados foram inseridos corretamente" -ForegroundColor White
Write-Host "" 
Write-Host "Script concluido! Os dados de teste estao prontos." -ForegroundColor Green