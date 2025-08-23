# User Profile Service

Microserviço responsável pelo gerenciamento de perfis de usuário, atividades, conquistas e estatísticas da aplicação NaMata.

## Funcionalidades

- **Gerenciamento de Perfis**: Criação, atualização e consulta de perfis de usuário
- **Atividades**: Registro e acompanhamento de atividades dos usuários
- **Sistema de Conquistas**: Gerenciamento de conquistas e progresso dos usuários
- **Badges/Insígnias**: Sistema de insígnias e recompensas
- **Estatísticas**: Coleta e análise de estatísticas dos usuários
- **Rankings**: Sistema de classificação e rankings

## Tecnologias

- **Java 17**
- **Spring Boot 3.2**
- **Spring Data JPA**
- **PostgreSQL**
- **Redis** (Cache)
- **Spring Security** (JWT)
- **OpenFeign** (Comunicação entre serviços)
- **Docker**

## Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/namata/userprofile/
│   │   ├── client/          # Clientes Feign para outros serviços
│   │   ├── config/          # Configurações da aplicação
│   │   ├── controller/      # Controllers REST
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── entity/         # Entidades JPA
│   │   ├── enums/          # Enumerações
│   │   ├── exception/      # Exceções customizadas
│   │   ├── repository/     # Repositories JPA
│   │   └── service/        # Serviços de negócio
│   └── resources/
│       ├── application.yml
│       └── application-docker.yml
```

## Configuração

### Variáveis de Ambiente

| Variável | Descrição | Padrão |
|----------|-----------|--------|
| `DB_HOST` | Host do PostgreSQL | `localhost` |
| `DB_PORT` | Porta do PostgreSQL | `5432` |
| `DB_NAME` | Nome do banco de dados | `namata_profile` |
| `DB_USERNAME` | Usuário do banco | `postgres` |
| `DB_PASSWORD` | Senha do banco | `admin` |
| `REDIS_HOST` | Host do Redis | `localhost` |
| `REDIS_PORT` | Porta do Redis | `6379` |
| `AUTH_SERVICE_URL` | URL do serviço de autenticação | `http://localhost:8081` |

## Executando a Aplicação

### Desenvolvimento Local

```bash
# Compilar e executar
./mvnw spring-boot:run

# Ou com profile específico
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Docker

```bash
# Build da imagem
docker build -t user-profile-service .

# Executar com docker-compose
docker-compose up -d
```

## API Endpoints

### Perfis de Usuário
- `POST /api/v1/profiles` - Criar perfil
- `GET /api/v1/profiles/user/{userId}` - Buscar perfil por usuário
- `PUT /api/v1/profiles/{profileId}` - Atualizar perfil
- `GET /api/v1/profiles/search` - Pesquisar perfis

### Atividades
- `POST /api/v1/activities` - Criar atividade
- `GET /api/v1/activities/user/{userId}` - Listar atividades do usuário
- `PUT /api/v1/activities/{activityId}` - Atualizar atividade
- `POST /api/v1/activities/{activityId}/like` - Curtir atividade

### Conquistas
- `GET /api/v1/achievements/user/{userId}` - Listar conquistas do usuário
- `POST /api/v1/achievements/{achievementId}/progress` - Atualizar progresso
- `POST /api/v1/achievements/check-automatic/{userId}` - Verificar conquistas automáticas

### Badges/Insígnias
- `GET /api/v1/badges` - Listar badges disponíveis
- `GET /api/v1/badges/type/{type}` - Buscar badges por tipo
- `POST /api/v1/badges/create-defaults` - Criar badges padrão

### Estatísticas
- `GET /api/v1/statistics/user/{userId}` - Estatísticas do usuário
- `GET /api/v1/statistics/rankings/points` - Ranking por pontos
- `GET /api/v1/statistics/rankings/trails` - Ranking por trilhas

## Documentação da API

Acesse a documentação Swagger em: `http://localhost:8082/swagger-ui.html`

## Monitoramento

- **Health Check**: `http://localhost:8082/actuator/health`
- **Métricas**: `http://localhost:8082/actuator/metrics`
- **Info**: `http://localhost:8082/actuator/info`

## Desenvolvimento

### Pré-requisitos

- Java 17+
- Maven 3.8+
- PostgreSQL 12+
- Redis 6+

### Configuração do Banco de Dados

```sql
CREATE DATABASE namata_profile;
CREATE USER namata_user WITH PASSWORD 'namata_pass';
GRANT ALL PRIVILEGES ON DATABASE namata_profile TO namata_user;
```

### Testes

```bash
# Executar todos os testes
./mvnw test

# Executar testes com coverage
./mvnw test jacoco:report
```

## Integração com Outros Serviços

Este serviço integra com:

- **Auth Service**: Validação de tokens JWT e informações de usuários
- **Trail Service**: Informações sobre trilhas para conquistas automáticas

## Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request