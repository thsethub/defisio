# DEFISIO - API Linfedemapp

## Visão Geral

O DEFISIO é uma API REST desenvolvida em Spring Boot para gerenciamento de pacientes com linfedema. O sistema permite o cadastro de usuários (profissionais de saúde), gerenciamento de pacientes e registro de medições para acompanhamento da condição clínica.

## Características Principais

- ✅ Autenticação JWT (JSON Web Tokens)
- ✅ Gestão de usuários profissionais de saúde
- ✅ Cadastro e acompanhamento de pacientes
- ✅ Registro de medições (volumetria e perimetria)
- ✅ Histórico completo de tratamentos
- ✅ API RESTful com documentação completa
- ✅ Banco de dados PostgreSQL
- ✅ Deploy com Docker e Docker Compose

---

## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.4.5**
- **Spring Security** (Autenticação JWT)
- **Spring Data JPA** (Persistência)
- **PostgreSQL** (Banco de dados)
- **Flyway** (Migração de banco)
- **Docker & Docker Compose** (Containerização)
- **Maven** (Gerenciamento de dependências)

---

## Como Iniciar o Projeto

### Pré-requisitos

- **Java 17** ou superior
- **Docker** e **Docker Compose**
- **Maven 3.6+** (opcional, se não usar Docker)

### Opção 1: Usando Docker (Recomendado)

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/thsethub/defisio.git
   cd defisio
   ```

2. **Inicie com o script automático:**
   ```bash
   chmod +x start.sh
   ./start.sh
   ```

   Ou manualmente:
   ```bash
   docker-compose down
   docker build -t defisio:latest ./
   docker-compose up --build --force-recreate --remove-orphans
   ```

3. **A API estará disponível em:**
   ```
   http://localhost:8083
   ```

### Opção 2: Desenvolvimento Local

1. **Configure o PostgreSQL:**
   ```bash
   # Inicie apenas o banco de dados
   docker-compose up postgres-db-defisio -d
   ```

2. **Configure o arquivo `application.properties`:**
   ```properties
   # Descomente a linha para desenvolvimento local
   spring.datasource.url=jdbc:postgresql://localhost:5433/linfedemapp
   spring.flyway.url=jdbc:postgresql://localhost:5433/linfedemapp
   ```

3. **Execute a aplicação:**
   ```bash
   mvn spring-boot:run
   ```

### Verificação da Instalação

Teste se a API está funcionando:
```bash
curl -X GET http://localhost:8083/auth/me
# Deve retornar 401 Unauthorized (comportamento esperado sem token)
```

---

## Documentação da API

### Base URL
```
http://localhost:8083
```

### Autenticação

A API utiliza autenticação JWT. Após o login, inclua o token no header:
```
Authorization: Bearer <seu-token-jwt>
```

---

## Endpoints

### 🔐 Autenticação (`/auth`)

#### 1. **Registro de Usuário**
```http
POST /auth/register
Content-Type: application/json
```

**Payload:**
```json
{
  "name": "Dr. João Silva",
  "idade": "35",
  "origem": "UFPE",
  "email": "joao.silva@exemplo.com",
  "telefone": "+55 81 99999-9999",
  "titulacao": "Fisioterapeuta",
  "password": "senha123"
}
```

**Resposta (201 Created):**
```json
{
  "email": "joao.silva@exemplo.com",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### 2. **Login**
```http
POST /auth/login
Content-Type: application/json
```

**Payload:**
```json
{
  "email": "joao.silva@exemplo.com",
  "password": "senha123"
}
```

**Resposta (200 OK):**
```json
{
  "email": "joao.silva@exemplo.com",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### 3. **Perfil do Usuário**
```http
GET /auth/me
Authorization: Bearer <token>
```

**Resposta (200 OK):**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "Dr. João Silva",
  "email": "joao.silva@exemplo.com",
  "idade": "35",
  "telefone": "+55 81 99999-9999",
  "origem": "UFPE",
  "titulacao": "Fisioterapeuta"
}
```

---

### 👤 Gerenciamento de Pacientes (`/api/pacientes`)

#### 1. **Adicionar Novo Paciente**
```http
POST /api/pacientes
Authorization: Bearer <token>
Content-Type: application/json
```

**Payload:**
```json
{
  "usuarioId": "123e4567-e89b-12d3-a456-426614174000",
  "nome": "Maria da Silva",
  "dataNascimento": "1980-05-15",
  "endereco": "Rua das Flores, 123, Recife-PE",
  "telefone": "+55 81 88888-8888",
  "pesoCorporal": "65.5",
  "altura": "1.65",
  "nivelAtividadeFisica": "Sedentário",
  "estadoCivil": "Casada",
  "ocupacao": "Professora",
  "dataDiagnostiCancer": "2023-01-15",
  "procedimentos": [
    "Mastectomia",
    "Quimioterapia"
  ],
  "alteracoesCutaneas": [
    "Cicatriz cirúrgica",
    "Edema"
  ],
  "queixasMusculoesqueleticas": "Dor no ombro direito",
  "sintomasLinfedema": "Inchaço no braço direito",
  "sinalCacifo": "Positivo",
  "sinalCascaLaranja": "Negativo",
  "sinalStemmer": "Positivo",
  "radioterapia": {
    "tipo": "Externa",
    "duracao": "6 semanas"
  },
  "cirurgia": {
    "tipo": "Mastectomia radical modificada",
    "duracao": "3 horas"
  },
  "hormonoterapia": {
    "tipo": "Tamoxifeno",
    "duracao": "5 anos"
  },
  "detalhesHormonoterapia": "Tamoxifeno 20mg/dia",
  "quimioterapia": {
    "tipo": "AC-T",
    "duracao": "4 ciclos"
  },
  "observacaoPaciente": "Paciente colaborativa, boa aderência ao tratamento"
}
```

**Resposta (200 OK):**
```json
{
  "id": "456e7890-e89b-12d3-a456-426614174001",
  "nome": "Maria da Silva",
  "dataNascimento": "1980-05-15",
  "endereco": "Rua das Flores, 123, Recife-PE",
  "telefone": "+55 81 88888-8888",
  "pesoCorporal": "65.5",
  "altura": "1.65",
  // ... outros campos
}
```

#### 2. **Listar Pacientes por Usuário**
```http
GET /api/pacientes/usuario/{usuarioId}
Authorization: Bearer <token>
```

**Resposta (200 OK):**
```json
[
  {
    "id": "456e7890-e89b-12d3-a456-426614174001",
    "nome": "Maria da Silva",
    "dataNascimento": "1980-05-15",
    "telefone": "+55 81 88888-8888",
    // ... outros campos básicos
  }
]
```

#### 3. **Buscar Paciente por ID**
```http
GET /api/pacientes/{pacienteId}
Authorization: Bearer <token>
```

**Resposta (200 OK):**
```json
{
  "id": "456e7890-e89b-12d3-a456-426614174001",
  "nome": "Maria da Silva",
  "dataNascimento": "1980-05-15",
  // ... dados completos do paciente
}
```

#### 4. **Deletar Paciente**
```http
DELETE /api/pacientes/{pacienteId}
Authorization: Bearer <token>
```

**Resposta (204 No Content)**

---

### 📊 Medições de Pacientes

#### 1. **Adicionar Medição ao Paciente**
```http
POST /api/pacientes/{pacienteId}/mensuracao
Authorization: Bearer <token>
Content-Type: application/json
```

**Payload:**
```json
{
  "tipoReferencia": "Braço esquerdo",
  "observacaoMedicao": "Medição realizada com paciente em pé",
  "volumetry": {
    "referenceArm": "Esquerdo",
    "affectedArm": "Direito",
    "volumesReferencia": [250.5, 280.3, 310.7],
    "volumesAfetado": [280.1, 320.5, 360.2],
    "volumeDifference": 15.2
  },
  "perimetry": {
    "pontosRef": "7cm, 14cm, 21cm",
    "leftArmInputs": ["25", "28", "30"],
    "rightArmInputs": ["28", "32", "35"],
    "leftArmComprimento": "45cm",
    "rightArmComprimento": "45cm",
    "differences": [3.0, 4.0, 5.0]
  }
}
```

**Resposta (200 OK):**
```json
{
  "id": "789e0123-e89b-12d3-a456-426614174002",
  "paciente": {
    "id": "456e7890-e89b-12d3-a456-426614174001",
    "nome": "Maria da Silva"
  },
  "dataAvaliacao": "2024-01-15",
  "tipoReferencia": "Braço esquerdo",
  "observacaoMedicao": "Medição realizada com paciente em pé",
  // ... dados completos da medição
}
```

#### 2. **Listar Medições do Paciente**
```http
GET /api/pacientes/usuario/{usuarioId}/{pacienteId}/mensuracoes
Authorization: Bearer <token>
```

**Resposta (200 OK):**
```json
[
  {
    "id": "789e0123-e89b-12d3-a456-426614174002",
    "dataAvaliacao": "2024-01-15",
    "tipoReferencia": "Braço esquerdo",
    "observacaoMedicao": "Medição realizada com paciente em pé",
    // ... dados das medições
  }
]
```

---

## Códigos de Status HTTP

| Código | Descrição |
|--------|-----------|
| `200` | OK - Sucesso |
| `201` | Created - Recurso criado |
| `204` | No Content - Sucesso sem retorno |
| `400` | Bad Request - Dados inválidos |
| `401` | Unauthorized - Token inválido/ausente |
| `403` | Forbidden - Acesso negado |
| `404` | Not Found - Recurso não encontrado |
| `500` | Internal Server Error - Erro no servidor |

---

## Estrutura de Dados

### Modelos Principais

#### User (Usuário)
```json
{
  "id": "UUID",
  "name": "string",
  "email": "string",
  "idade": "string",
  "telefone": "string",
  "origem": "string",
  "titulacao": "string",
  "password": "string (hash)"
}
```

#### Paciente
```json
{
  "id": "UUID",
  "nome": "string",
  "dataNascimento": "string",
  "endereco": "string",
  "telefone": "string",
  "pesoCorporal": "string",
  "altura": "string",
  "nivelAtividadeFisica": "string",
  "estadoCivil": "string",
  "ocupacao": "string",
  "dataDiagnostiCancer": "string",
  "procedimentos": ["string"],
  "alteracoesCutaneas": ["string"],
  "queixasMusculoesqueleticas": "string",
  "sintomasLinfedema": "string",
  "sinalCacifo": "string",
  "sinalCascaLaranja": "string",
  "sinalStemmer": "string",
  "radioterapia": "ProcedimentoDetalhado",
  "cirurgia": "ProcedimentoDetalhado",
  "disseccaoAxilar": "ProcedimentoDetalhado",
  "hormonoterapia": "ProcedimentoDetalhado",
  "detalhesHormonoterapia": "string",
  "quimioterapia": "ProcedimentoDetalhado",
  "observacaoPaciente": "string",
  "usuario": "User"
}
```

#### ProcedimentoDetalhado
```json
{
  "tipo": "string",
  "duracao": "string"
}
```

#### DadosMensuracao
```json
{
  "id": "UUID",
  "paciente": "Paciente",
  "dataAvaliacao": "LocalDate",
  "tipoReferencia": "string",
  "observacaoMedicao": "string",
  "referenceArm": "string",
  "affectedArm": "string",
  "volumesReferencia": ["Double"],
  "volumesAfetado": ["Double"],
  "pontosRef": "string",
  "leftArmInputs": ["string"],
  "rightArmInputs": ["string"],
  "leftArmComprimento": "string",
  "rightArmComprimento": "string",
  "differences": ["Double"]
}
```

---

## Configuração e Customização

### Variáveis de Ambiente

| Variável | Descrição | Padrão |
|----------|-----------|--------|
| `SERVER_PORT` | Porta da aplicação | `8083` |
| `SPRING_DATASOURCE_URL` | URL do banco PostgreSQL | `jdbc:postgresql://postgres-db-defisio:5432/linfedemapp` |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco | `linfedemapp` |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco | `linfedemapp` |
| `API_SECURITY_TOKEN_TOKEN` | Chave secreta JWT | `my-secret-key2` |

### Configuração do Banco de Dados

O projeto utiliza PostgreSQL com Flyway para migrações. As configurações estão em:
- `src/main/resources/application.properties`
- Scripts de migração em `src/main/resources/db/migration/`

### Configuração de Segurança

- **JWT Token Expiration:** 30 dias
- **Password Encoding:** BCrypt
- **Endpoints Públicos:** `/auth/login`, `/auth/register`
- **Endpoints Protegidos:** Todos os demais

---

## Desenvolvimento

### Estrutura do Projeto

```
src/main/java/com/ufpe/defisio/linfedemapp/
├── controllers/          # Controladores REST
├── domain/              # Entidades de domínio
├── dto/                 # Data Transfer Objects
├── infra/               # Configurações de infraestrutura
├── repositories/        # Repositórios JPA
├── services/           # Lógica de negócio
└── LinfedemappApplication.java
```

### Comandos Úteis

```bash
# Compilar o projeto
mvn clean compile

# Executar testes
mvn test

# Criar package
mvn clean package

# Executar a aplicação
mvn spring-boot:run

# Ver logs do Docker
docker-compose logs -f defisio-spring-app

# Parar todos os serviços
docker-compose down

# Rebuild completo
docker-compose up --build --force-recreate
```

---

## Troubleshooting

### Problemas Comuns

1. **Erro de conexão com banco:**
   ```bash
   # Verifique se o PostgreSQL está rodando
   docker-compose ps
   
   # Reinicie apenas o banco
   docker-compose restart postgres-db-defisio
   ```

2. **Token JWT inválido:**
   ```bash
   # Verifique se o token está sendo enviado corretamente
   # O token expira em 30 dias, faça login novamente se necessário
   ```

3. **Porta 8083 em uso:**
   ```bash
   # Altere a porta no docker-compose.yml ou mate o processo
   lsof -ti:8083 | xargs kill -9
   ```

4. **Problemas de compilação Java:**
   ```bash
   # Verifique a versão do Java
   java --version
   # Deve ser Java 17 ou superior
   ```

### Logs

Para debug, verifique os logs:
```bash
# Logs da aplicação
docker-compose logs defisio-spring-app

# Logs do banco
docker-compose logs postgres-db-defisio

# Logs em tempo real
docker-compose logs -f
```

---

## Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

---

## Licença

Este projeto está licenciado sob a licença [MIT](LICENSE).

---

## Contato

Para dúvidas ou suporte:
- **Email:** [Inserir email de contato]
- **GitHub:** [thsethub](https://github.com/thsethub)
- **Projeto:** [DEFISIO](https://github.com/thsethub/defisio)

---

**Versão da API:** 0.0.1-SNAPSHOT  
**Última atualização:** Dezembro 2024
