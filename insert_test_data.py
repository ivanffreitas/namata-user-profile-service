#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script para inserir dados de teste no banco PostgreSQL
Execute: python insert_test_data.py
"""

import psycopg2
from datetime import datetime, date
import sys

# Configurações do banco de dados
DB_CONFIG = {
    'host': 'localhost',
    'port': 5433,
    'database': 'namata_profile',
    'user': 'postgres',
    'password': 'admin'
}

def create_connection():
    """Cria conexão com o banco PostgreSQL"""
    try:
        conn = psycopg2.connect(**DB_CONFIG)
        return conn
    except psycopg2.Error as e:
        print(f"Erro ao conectar ao banco: {e}")
        return None

def create_tables(conn):
    """Cria as tabelas se não existirem"""
    cursor = conn.cursor()
    
    # Criar tabela user_profiles
    cursor.execute("""
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
    """)
    
    # Criar tabela user_profile_interests
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS user_profile_interests (
            user_profile_id BIGINT NOT NULL,
            interest VARCHAR(50) NOT NULL,
            FOREIGN KEY (user_profile_id) REFERENCES user_profiles(id) ON DELETE CASCADE
        );
    """)
    
    # Criar tabela statistics
    cursor.execute("""
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
    """)
    
    conn.commit()
    cursor.close()
    print("Tabelas criadas com sucesso!")

def insert_user_profiles(conn):
    """Insere dados de teste na tabela user_profiles"""
    cursor = conn.cursor()
    
    users_data = [
        (101, 'Carlos Aventureiro', 'Explorador experiente das trilhas cariocas', 'https://example.com/avatar101.jpg', date(1985, 3, 15), 'MALE', 'Rio de Janeiro, RJ', '+5521987654321', 'EXPERT', 'ADVENTURE', 'PUBLIC', True, True),
        (102, 'Ana Montanhista', 'Apaixonada por montanhas e natureza', 'https://example.com/avatar102.jpg', date(1990, 7, 22), 'FEMALE', 'Belo Horizonte, MG', '+5531987654322', 'EXPERT', 'HIKING', 'PUBLIC', True, True),
        (103, 'Pedro Trilheiro', 'Descobrindo novas trilhas em SP', 'https://example.com/avatar103.jpg', date(1988, 11, 8), 'MALE', 'São Paulo, SP', '+5511987654323', 'ADVANCED', 'HIKING', 'PUBLIC', True, False),
        (104, 'Maria Natureza', 'Observadora da fauna e flora', 'https://example.com/avatar104.jpg', date(1992, 5, 30), 'FEMALE', 'Curitiba, PR', '+5541987654324', 'ADVANCED', 'NATURE_OBSERVATION', 'PUBLIC', True, True),
        (105, 'João Explorador', 'Aventuras em busca do desconhecido', 'https://example.com/avatar105.jpg', date(1987, 9, 12), 'MALE', 'Porto Alegre, RS', '+5551987654325', 'EXPERT', 'ADVENTURE', 'PUBLIC', True, False),
        (106, 'Fernanda Caminhante', 'Trilhas são minha paixão', 'https://example.com/avatar106.jpg', date(1991, 12, 3), 'FEMALE', 'Salvador, BA', '+5571987654326', 'INTERMEDIATE', 'HIKING', 'PUBLIC', True, False),
        (107, 'Roberto Montanha', 'Conquistando picos e montanhas', 'https://example.com/avatar107.jpg', date(1984, 6, 18), 'MALE', 'Fortaleza, CE', '+5585987654327', 'ADVANCED', 'HIKING', 'PUBLIC', True, True),
        (108, 'Camila Rocha', 'Fotógrafa de natureza', 'https://example.com/avatar108.jpg', date(1993, 4, 25), 'FEMALE', 'Brasília, DF', '+5561987654328', 'EXPERT', 'PHOTOGRAPHY', 'PUBLIC', True, False),
        (109, 'Lucas Verde', 'Ecoturismo e sustentabilidade', 'https://example.com/avatar109.jpg', date(1989, 8, 7), 'MALE', 'Recife, PE', '+5581987654329', 'INTERMEDIATE', 'RESEARCH', 'PUBLIC', True, False),
        (110, 'Juliana Trilha', 'Sempre em busca de novas aventuras', 'https://example.com/avatar110.jpg', date(1986, 1, 14), 'FEMALE', 'Manaus, AM', '+5592987654330', 'ADVANCED', 'ADVENTURE', 'PUBLIC', True, True),
        (111, 'Diego Aventura', 'Trilheiro de fim de semana', 'https://example.com/avatar111.jpg', date(1994, 10, 29), 'MALE', 'Goiânia, GO', '+5562987654331', 'INTERMEDIATE', 'HIKING', 'PUBLIC', True, False),
        (112, 'Patrícia Natureza', 'Conectada com a natureza', 'https://example.com/avatar112.jpg', date(1988, 3, 16), 'FEMALE', 'Florianópolis, SC', '+5548987654332', 'ADVANCED', 'RELAXATION', 'PUBLIC', True, True),
        (113, 'Rafael Pico', 'Escalador e montanhista', 'https://example.com/avatar113.jpg', date(1983, 7, 21), 'MALE', 'Vitória, ES', '+5527987654333', 'EXPERT', 'ADVENTURE', 'PUBLIC', True, False),
        (114, 'Carla Caminho', 'Trilhas urbanas e rurais', 'https://example.com/avatar114.jpg', date(1995, 11, 5), 'FEMALE', 'Campo Grande, MS', '+5567987654334', 'BEGINNER', 'HIKING', 'PUBLIC', True, False),
        (115, 'Thiago Mata', 'Preservação e aventura', 'https://example.com/avatar115.jpg', date(1990, 2, 28), 'MALE', 'Belém, PA', '+5591987654335', 'INTERMEDIATE', 'RESEARCH', 'PUBLIC', True, True)
    ]
    
    for user_data in users_data:
        cursor.execute("""
            INSERT INTO user_profiles (
                user_id, display_name, bio, profile_picture_url, date_of_birth, gender, 
                location, phone_number, experience_level, exploration_type, privacy_level, 
                is_active, is_verified
            ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            ON CONFLICT (user_id) DO NOTHING
        """, user_data)
    
    conn.commit()
    cursor.close()
    print(f"Inseridos {len(users_data)} usuários!")

def insert_statistics(conn):
    """Insere dados de estatísticas"""
    cursor = conn.cursor()
    
    # Buscar IDs dos usuários criados
    cursor.execute("SELECT id, user_id FROM user_profiles ORDER BY user_id")
    user_profiles = cursor.fetchall()
    
    stats_data = [
        (180, 3500.50, 21600, 85000.0, 45, 2800, 320, 85, 1250, 340, 45, 25000, 15, 28, 450, 280, 12, 1, 1),
        (165, 3200.80, 19800, 78000.0, 42, 2650, 290, 78, 1180, 315, 42, 23500, 12, 25, 420, 260, 11, 2, 1),
        (150, 2900.30, 18000, 72000.0, 38, 2500, 260, 72, 1100, 290, 38, 22000, 10, 22, 380, 240, 10, 3, 2),
        (140, 2650.70, 16800, 65000.0, 35, 2350, 240, 65, 980, 265, 35, 20500, 8, 20, 350, 220, 9, 4, 2),
        (130, 2400.20, 15600, 58000.0, 32, 2200, 220, 58, 920, 240, 32, 19000, 7, 18, 320, 200, 8, 5, 3),
        (120, 2150.90, 14400, 52000.0, 30, 2050, 200, 52, 850, 220, 28, 17500, 6, 16, 290, 180, 7, 6, 3),
        (110, 1900.50, 13200, 45000.0, 28, 1900, 180, 45, 780, 195, 25, 16000, 5, 14, 260, 160, 6, 7, 4),
        (100, 1650.80, 12000, 38000.0, 25, 1750, 160, 38, 720, 170, 22, 14500, 4, 12, 230, 140, 5, 8, 4),
        (90, 1400.30, 10800, 32000.0, 22, 1600, 140, 32, 650, 145, 18, 13000, 3, 10, 200, 120, 4, 9, 5),
        (80, 1150.70, 9600, 25000.0, 20, 1450, 120, 25, 580, 120, 15, 11500, 2, 8, 170, 100, 3, 10, 5),
        (70, 900.20, 8400, 20000.0, 18, 1300, 100, 20, 520, 95, 12, 10000, 1, 6, 140, 80, 2, 11, 6),
        (60, 750.50, 7200, 15000.0, 15, 1150, 80, 15, 450, 70, 10, 8500, 0, 4, 110, 60, 1, 12, 6),
        (50, 600.80, 6000, 12000.0, 12, 1000, 60, 12, 380, 45, 8, 7000, 0, 2, 80, 40, 0, 13, 7),
        (40, 450.30, 4800, 8000.0, 10, 850, 40, 8, 320, 20, 6, 5500, 0, 1, 50, 20, 0, 14, 7),
        (30, 300.70, 3600, 5000.0, 8, 700, 20, 5, 250, 10, 4, 4000, 0, 0, 20, 10, 0, 15, 8)
    ]
    
    for i, (profile_id, user_id) in enumerate(user_profiles):
        if i < len(stats_data):
            stat_data = stats_data[i]
            cursor.execute("""
                INSERT INTO statistics (
                    user_profile_id, total_trails_completed, total_distance_km, total_time_minutes, 
                    total_elevation_gain_m, longest_trail_km, highest_elevation_m, total_photos_shared, 
                    total_reviews_posted, total_likes_received, total_comments_received, 
                    total_badges_earned, total_points, current_streak, longest_streak, 
                    total_followers, total_following, total_guides_booked, global_rank, local_rank,
                    last_activity_at
                ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                ON CONFLICT (user_profile_id) DO NOTHING
            """, (profile_id,) + stat_data + (datetime.now(),))
    
    conn.commit()
    cursor.close()
    print(f"Inseridas {len(stats_data)} estatísticas!")

def verify_data(conn):
    """Verifica os dados inseridos"""
    cursor = conn.cursor()
    
    # Contar usuários
    cursor.execute("SELECT COUNT(*) FROM user_profiles")
    user_count = cursor.fetchone()[0]
    print(f"Total de usuários: {user_count}")
    
    # Contar estatísticas
    cursor.execute("SELECT COUNT(*) FROM statistics")
    stats_count = cursor.fetchone()[0]
    print(f"Total de estatísticas: {stats_count}")
    
    # Top 5 ranking
    cursor.execute("""
        SELECT 
            up.display_name,
            up.location,
            up.experience_level,
            s.total_points,
            s.total_trails_completed,
            s.global_rank
        FROM user_profiles up
        JOIN statistics s ON up.id = s.user_profile_id
        WHERE up.is_active = true
        ORDER BY s.total_points DESC
        LIMIT 5
    """)
    
    print("\nTop 5 Ranking:")
    for row in cursor.fetchall():
        print(f"{row[0]} ({row[1]}) - {row[3]} pontos - Rank #{row[5]}")
    
    cursor.close()

def main():
    """Função principal"""
    print("Conectando ao banco PostgreSQL...")
    conn = create_connection()
    
    if not conn:
        print("Falha na conexão. Verifique se o PostgreSQL está rodando na porta 5433.")
        sys.exit(1)
    
    try:
        print("Criando tabelas...")
        create_tables(conn)
        
        print("Inserindo usuários...")
        insert_user_profiles(conn)
        
        print("Inserindo estatísticas...")
        insert_statistics(conn)
        
        print("Verificando dados...")
        verify_data(conn)
        
        print("\n✅ Dados de teste inseridos com sucesso!")
        
    except Exception as e:
        print(f"❌ Erro durante a execução: {e}")
        conn.rollback()
    
    finally:
        conn.close()

if __name__ == "__main__":
    main()