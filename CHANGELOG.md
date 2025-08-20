# Changelog

## [0.0.1-SNAPSHOT] - 2024-12-20

### Adicionado
- ✅ API completa para gerenciamento de pacientes com linfedema
- ✅ Sistema de autenticação JWT
- ✅ Controladores para autenticação (`AuthController`)
- ✅ Controladores para pacientes (`PacienteController`)
- ✅ Modelos de dados para pacientes e medições
- ✅ Integração com banco PostgreSQL
- ✅ Migração de dados com Flyway
- ✅ Configuração Docker e Docker Compose
- ✅ Documentação completa da API
- ✅ Exemplos de uso e scripts de teste

### Endpoints Implementados

#### Autenticação (`/auth`)
- `POST /auth/register` - Registro de novos usuários
- `POST /auth/login` - Login de usuários
- `GET /auth/me` - Perfil do usuário logado

#### Pacientes (`/api/pacientes`)
- `POST /api/pacientes` - Adicionar novo paciente
- `GET /api/pacientes/usuario/{usuarioId}` - Listar pacientes por usuário
- `GET /api/pacientes/{pacienteId}` - Buscar paciente por ID
- `DELETE /api/pacientes/{pacienteId}` - Deletar paciente
- `POST /api/pacientes/{pacienteId}/mensuracao` - Adicionar medição
- `GET /api/pacientes/usuario/{usuarioId}/{pacienteId}/mensuracoes` - Listar medições

### Configurações
- ✅ Java 17 compatibility
- ✅ Spring Boot 3.4.5
- ✅ PostgreSQL database
- ✅ JWT authentication with 30-day expiration
- ✅ Docker containerization
- ✅ CORS configuration

### Segurança
- ✅ Senhas criptografadas com BCrypt
- ✅ Tokens JWT com informações do usuário
- ✅ Endpoints protegidos por autenticação
- ✅ Filtro de segurança personalizado

### Documentação
- ✅ README.md completo com instruções de instalação
- ✅ Documentação de todos os endpoints
- ✅ Exemplos de uso em curl, bash e Python
- ✅ Guia de troubleshooting
- ✅ Scripts de teste automatizado

## Próximas Versões (Roadmap)

### [0.1.0] - Funcionalidades Planejadas
- [ ] Endpoint PUT para atualização de pacientes
- [ ] Endpoint PUT para atualização de usuários
- [ ] Paginação nas listagens
- [ ] Filtros de busca avançada
- [ ] Validação de dados mais robusta
- [ ] Logging estruturado
- [ ] Métricas de API (Actuator)

### [0.2.0] - Melhorias de Segurança
- [ ] Refresh tokens
- [ ] Rate limiting
- [ ] Auditoria de ações
- [ ] Criptografia de dados sensíveis
- [ ] Roles e permissões avançadas

### [0.3.0] - Funcionalidades Clínicas
- [ ] Relatórios de progresso automáticos
- [ ] Gráficos de evolução das medições
- [ ] Alertas para piora do quadro
- [ ] Integração com protocolos clínicos
- [ ] Export de dados em PDF/Excel

### [1.0.0] - Versão de Produção
- [ ] Performance otimizada
- [ ] Testes de carga
- [ ] Monitoramento completo
- [ ] Backup automático
- [ ] Alta disponibilidade
- [ ] Documentação completa para produção

## Correções de Bugs

### [0.0.1-SNAPSHOT]
- ✅ Corrigida compatibilidade do Java (21 → 17)
- ✅ Corrigida configuração do Dockerfile para Java 17
- ✅ Verificada compilação e build do projeto

## Notas Técnicas

### Estrutura do Banco de Dados
- Tabela `users` - Dados dos profissionais de saúde
- Tabela `pacientes` - Dados dos pacientes
- Tabela `dados_mensuracao` - Histórico de medições

### Arquitetura
- **Controller Layer** - Recebe requisições HTTP
- **Service Layer** - Lógica de negócio
- **Repository Layer** - Acesso a dados
- **Security Layer** - Autenticação e autorização
- **DTO Layer** - Transferência de dados

### Dependências Principais
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- PostgreSQL Driver
- JWT Library (auth0)
- Flyway Core
- Lombok

## Contribuidores
- Desenvolvedor Principal: [@thsethub](https://github.com/thsethub)
- Documentação: Sistema automatizado