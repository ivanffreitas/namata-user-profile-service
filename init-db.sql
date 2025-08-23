-- Script de inicialização do banco de dados para User Profile Service

-- Criar extensões necessárias
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "postgis";

-- Criar schema se não existir
CREATE SCHEMA IF NOT EXISTS public;

-- Comentários sobre as tabelas que serão criadas pelo Hibernate
COMMENT ON SCHEMA public IS 'Schema principal para o User Profile Service';

-- Inserir dados iniciais de badges padrão (será executado após a criação das tabelas)
-- Estes dados serão inseridos via service após a inicialização da aplicação