-- Migração para simplificar a tabela user_trail_completions
-- Remove campos desnecessários e mantém apenas os essenciais

-- Primeiro, criar uma tabela temporária com a nova estrutura
CREATE TABLE user_trail_completions_new (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_profile_id UUID NOT NULL,
    trail_id UUID NOT NULL,
    completed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_user_trail_completions_user_profile 
        FOREIGN KEY (user_profile_id) REFERENCES user_profiles(id) ON DELETE CASCADE,
    
    CONSTRAINT uk_user_trail_completion 
        UNIQUE (user_profile_id, trail_id)
);

-- Criar índices para otimizar consultas
CREATE INDEX idx_user_trail_completions_user_profile_id ON user_trail_completions_new(user_profile_id);
CREATE INDEX idx_user_trail_completions_trail_id ON user_trail_completions_new(trail_id);
CREATE INDEX idx_user_trail_completions_completed_at ON user_trail_completions_new(completed_at);

-- Migrar dados existentes (se houver)
INSERT INTO user_trail_completions_new (id, user_profile_id, trail_id, completed_at)
SELECT id, user_profile_id, trail_id, 
       COALESCE(completed_at, CURRENT_TIMESTAMP) as completed_at
FROM user_trail_completions;

-- Remover a tabela antiga
DROP TABLE user_trail_completions;

-- Renomear a nova tabela
ALTER TABLE user_trail_completions_new RENAME TO user_trail_completions;