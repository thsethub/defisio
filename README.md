# defisio-api

API REST do Linfedemapp, desenvolvida em Spring Boot. Gerencia autenticação de usuários, cadastro de pacientes e registro de mensurações para acompanhamento de linfedema.

## Stack

- Java 21
- Spring Boot 3.4.5
- Spring Security + JWT (auth0 java-jwt 4.4)
- PostgreSQL 16 + Flyway (migrações)
- Spring Boot Actuator (health check)
- Ngrok (exposição pública via tunnel)
- Docker

## Endpoints

### Autenticação — `/auth`

| Método | Rota | Auth | Descrição |
|---|---|---|---|
| `POST` | `/auth/login` | — | Login com e-mail e senha |
| `POST` | `/auth/register` | — | Cadastro de novo usuário |
| `GET` | `/auth/me` | JWT | Dados do usuário autenticado |
| `POST` | `/auth/forgot-password` | — | Solicita código de redefinição (enviado por e-mail) |
| `POST` | `/auth/verify-code` | — | Valida o código de 6 dígitos |
| `POST` | `/auth/reset-password` | — | Redefine a senha com código validado |

### Pacientes — `/api/pacientes`

| Método | Rota | Auth | Descrição |
|---|---|---|---|
| `POST` | `/api/pacientes` | JWT | Cadastra novo paciente |
| `GET` | `/api/pacientes/{id}` | JWT | Busca paciente por ID |
| `GET` | `/api/pacientes/usuario/{usuarioId}` | JWT | Lista pacientes de um usuário |
| `DELETE` | `/api/pacientes/{id}` | JWT | Remove paciente e suas mensurações |
| `POST` | `/api/pacientes/{id}/mensuracao` | JWT | Registra nova mensuração |
| `GET` | `/api/pacientes/usuario/{usuarioId}/{pacienteId}/mensuracoes` | JWT | Lista mensurações de um paciente |

### Health

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/actuator/health` | Status da aplicação (usado pelo Docker healthcheck) |

## Desenvolvimento local

### Pré-requisitos

- Java 21+
- Maven
- PostgreSQL rodando em `localhost:5433`

### Variáveis de ambiente

Copie o exemplo e preencha:

```bash
cp .env.example .env
```

| Variável | Obrigatório | Descrição |
|---|---|---|
| `NGROK_AUTHTOKEN` | Sim | Token do Ngrok |
| `POSTGRES_USER` | Sim | Usuário do banco |
| `POSTGRES_PASSWORD` | Sim | Senha do banco |
| `POSTGRES_DB` | Sim | Nome do banco |
| `JWT_SECRET` | Sim | Secret para assinatura JWT (mín. 32 chars) |
| `MAIL_USERNAME` | Não | Remetente de e-mail (default: `noreply.linfedemapp@gmail.com`) |
| `MAIL_PASSWORD` | Sim | Gmail App Password |

### Executar

```bash
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8083`. Health check: `http://localhost:8083/actuator/health`.

## Autenticação

Todas as rotas protegidas requerem o header:

```
Authorization: Bearer <token>
```

O token é retornado no login e no cadastro. Expiração: **30 dias**.

## Estrutura do projeto

```
src/main/java/com/ufpe/defisio/linfedemapp/
├── controllers/        # AuthController, PacienteController
├── domain/             # Entidades JPA (User, Paciente, DadosMensuracao)
├── dto/                # DTOs de request e response
├── infra/
│   ├── cors/           # Configuração CORS
│   └── security/       # JWT filter, token service, security config
├── repositories/       # Interfaces JPA
└── services/           # PacienteService, EmailService, PasswordResetService
```

## Build e publicação da imagem

O push para `main` ou `master` aciona o workflow do GitHub Actions que compila e publica a imagem em `ghcr.io/thsethub/defisio-api:latest`. Não são necessários secrets manuais — usa `GITHUB_TOKEN` automaticamente.

```bash
git push origin main
```
