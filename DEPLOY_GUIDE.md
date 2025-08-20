# Guia de Deploy para Produção - DEFISIO API

Este guia fornece instruções detalhadas para fazer deploy da API DEFISIO em ambiente de produção.

## Pré-requisitos para Produção

### Recursos Mínimos Recomendados
- **CPU:** 2 cores
- **RAM:** 4GB
- **Armazenamento:** 20GB SSD
- **Rede:** Conexão estável com HTTPS

### Software Necessário
- Docker 20.0+
- Docker Compose 2.0+
- Nginx (para proxy reverso)
- Certificado SSL válido

## Configuração de Produção

### 1. Variáveis de Ambiente

Crie um arquivo `.env.prod` com as configurações de produção:

```bash
# .env.prod
POSTGRES_USER=linfedemapp_prod
POSTGRES_PASSWORD=SENHA_SUPER_FORTE_AQUI
POSTGRES_DB=linfedemapp_prod

SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-db-defisio:5432/linfedemapp_prod
SPRING_DATASOURCE_USERNAME=linfedemapp_prod
SPRING_DATASOURCE_PASSWORD=SENHA_SUPER_FORTE_AQUI

# IMPORTANTE: Gere uma chave secreta forte para JWT
JWT_SECRET=SUA_CHAVE_SECRETA_JWT_MUITO_FORTE_E_LONGA_AQUI_MIN_256_BITS

# Configurações de produção
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8083

# PostgreSQL específico para produção
POSTGRES_PORT=5432
```

### 2. Docker Compose para Produção

Crie um arquivo `docker-compose.prod.yml`:

```yaml
version: '3.8'

services:
  postgres-db-defisio:
    image: postgres:15-alpine
    restart: unless-stopped
    environment:
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
      POSTGRES_DB: ${POSTGRES_DB}
    volumes:
      - postgres_data_prod:/var/lib/postgresql/data
      - ./backup:/backup
    networks:
      - defisio-network
    ports:
      - "127.0.0.1:5432:5432"  # Apenas localhost
    command: postgres -c max_connections=200 -c shared_buffers=256MB

  defisio-spring-app:
    build:
      context: .
      dockerfile: Dockerfile
    restart: unless-stopped
    environment:
      SPRING_DATASOURCE_URL: ${SPRING_DATASOURCE_URL}
      SPRING_DATASOURCE_USERNAME: ${SPRING_DATASOURCE_USERNAME}
      SPRING_DATASOURCE_PASSWORD: ${SPRING_DATASOURCE_PASSWORD}
      API_SECURITY_TOKEN_TOKEN: ${JWT_SECRET}
      SPRING_PROFILES_ACTIVE: ${SPRING_PROFILES_ACTIVE}
    depends_on:
      - postgres-db-defisio
    networks:
      - defisio-network
    ports:
      - "127.0.0.1:8083:8083"  # Apenas localhost
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8083/auth/me"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s

volumes:
  postgres_data_prod:
    driver: local

networks:
  defisio-network:
    driver: bridge
```

### 3. Configuração do Nginx

Crie `/etc/nginx/sites-available/defisio`:

```nginx
upstream defisio_backend {
    server 127.0.0.1:8083;
}

server {
    listen 80;
    server_name api.seudominio.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name api.seudominio.com;

    # Certificados SSL
    ssl_certificate /path/to/ssl/certificate.crt;
    ssl_certificate_key /path/to/ssl/private.key;
    
    # Configurações SSL modernas
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512:ECDHE-RSA-AES256-GCM-SHA384:DHE-RSA-AES256-GCM-SHA384;
    ssl_prefer_server_ciphers off;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;

    # Headers de segurança
    add_header X-Frame-Options DENY;
    add_header X-Content-Type-Options nosniff;
    add_header X-XSS-Protection "1; mode=block";
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;

    # Logs
    access_log /var/log/nginx/defisio_access.log;
    error_log /var/log/nginx/defisio_error.log;

    # Rate limiting
    limit_req_zone $binary_remote_addr zone=defisio_api:10m rate=30r/m;
    
    location / {
        limit_req zone=defisio_api burst=10 nodelay;
        
        proxy_pass http://defisio_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # Timeouts
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
        
        # CORS (se necessário)
        add_header Access-Control-Allow-Origin "*" always;
        add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS" always;
        add_header Access-Control-Allow-Headers "DNT,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Range,Authorization" always;
        
        if ($request_method = 'OPTIONS') {
            return 204;
        }
    }

    # Health check endpoint
    location /health {
        access_log off;
        proxy_pass http://defisio_backend/auth/me;
        proxy_set_header Host $host;
    }
}
```

## Scripts de Deploy

### 1. Script de Deploy Automático

Crie `deploy.sh`:

```bash
#!/bin/bash
set -e

echo "🚀 Iniciando deploy da API DEFISIO..."

# Verificar se está no diretório correto
if [ ! -f "docker-compose.prod.yml" ]; then
    echo "❌ Arquivo docker-compose.prod.yml não encontrado!"
    exit 1
fi

# Fazer backup do banco (se existir)
if [ "$(docker ps -q -f name=postgres-db-defisio)" ]; then
    echo "💾 Fazendo backup do banco de dados..."
    docker exec postgres-db-defisio pg_dump -U linfedemapp_prod linfedemapp_prod > "backup/backup_$(date +%Y%m%d_%H%M%S).sql"
fi

# Parar serviços antigos
echo "🛑 Parando serviços..."
docker-compose -f docker-compose.prod.yml down

# Construir nova imagem
echo "🔨 Construindo nova imagem..."
docker-compose -f docker-compose.prod.yml build --no-cache

# Iniciar serviços
echo "▶️ Iniciando serviços..."
docker-compose -f docker-compose.prod.yml up -d

# Aguardar inicialização
echo "⏳ Aguardando inicialização..."
sleep 30

# Verificar saúde dos serviços
echo "🏥 Verificando saúde dos serviços..."
if curl -f http://localhost:8083/auth/me; then
    echo "✅ API está respondendo!"
else
    echo "❌ API não está respondendo!"
    echo "📋 Logs da aplicação:"
    docker-compose -f docker-compose.prod.yml logs defisio-spring-app
    exit 1
fi

# Recarregar Nginx
echo "🔄 Recarregando Nginx..."
sudo nginx -t && sudo systemctl reload nginx

echo "🎉 Deploy concluído com sucesso!"
echo "🌐 API disponível em: https://api.seudominio.com"
```

### 2. Script de Monitoramento

Crie `monitor.sh`:

```bash
#!/bin/bash

# Função para verificar saúde da API
check_api_health() {
    if curl -s -f http://localhost:8083/auth/me > /dev/null; then
        echo "✅ API saudável"
        return 0
    else
        echo "❌ API não está respondendo"
        return 1
    fi
}

# Função para verificar banco de dados
check_database() {
    if docker exec postgres-db-defisio pg_isready -U linfedemapp_prod > /dev/null 2>&1; then
        echo "✅ Banco de dados saudável"
        return 0
    else
        echo "❌ Banco de dados não está respondendo"
        return 1
    fi
}

# Função para verificar uso de recursos
check_resources() {
    echo "📊 Uso de recursos:"
    echo "CPU: $(top -bn1 | grep "Cpu(s)" | awk '{print $2}' | sed 's/%us,//')"
    echo "RAM: $(free -h | awk '/^Mem:/ {print $3 "/" $2}')"
    echo "Disk: $(df -h / | awk 'NR==2{print $3 "/" $2 " (" $5 ")"}')"
}

# Função para backup automático
auto_backup() {
    echo "💾 Iniciando backup automático..."
    timestamp=$(date +%Y%m%d_%H%M%S)
    docker exec postgres-db-defisio pg_dump -U linfedemapp_prod linfedemapp_prod > "backup/auto_backup_${timestamp}.sql"
    
    # Manter apenas os últimos 7 backups
    ls -t backup/auto_backup_*.sql | tail -n +8 | xargs -r rm
    
    echo "✅ Backup concluído: auto_backup_${timestamp}.sql"
}

# Menu principal
case "$1" in
    "health")
        check_api_health && check_database
        ;;
    "resources")
        check_resources
        ;;
    "backup")
        auto_backup
        ;;
    "logs")
        docker-compose -f docker-compose.prod.yml logs -f --tail=100
        ;;
    "restart")
        docker-compose -f docker-compose.prod.yml restart
        ;;
    *)
        echo "Uso: $0 {health|resources|backup|logs|restart}"
        exit 1
        ;;
esac
```

## Configurações de Segurança

### 1. Firewall (UFW)

```bash
# Permitir apenas as portas necessárias
sudo ufw allow 22/tcp    # SSH
sudo ufw allow 80/tcp    # HTTP
sudo ufw allow 443/tcp   # HTTPS
sudo ufw deny 8083/tcp   # API (apenas localhost)
sudo ufw deny 5432/tcp   # PostgreSQL (apenas localhost)
sudo ufw enable
```

### 2. Configuração SSL com Let's Encrypt

```bash
# Instalar certbot
sudo apt update
sudo apt install certbot python3-certbot-nginx

# Obter certificado
sudo certbot --nginx -d api.seudominio.com

# Configurar renovação automática
sudo crontab -e
# Adicionar linha:
# 0 12 * * * /usr/bin/certbot renew --quiet
```

## Monitoramento e Logs

### 1. Configuração de Logs

Adicione ao `application.properties` para produção:

```properties
# Logs para produção
logging.level.com.ufpe.defisio=INFO
logging.level.org.springframework.security=WARN
logging.level.org.hibernate.SQL=WARN

logging.file.name=/app/logs/defisio.log
logging.file.max-size=10MB
logging.file.max-history=30

# Pattern de log estruturado
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
```

### 2. Monitoramento com Prometheus (Opcional)

Adicione ao `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

## Backup e Recuperação

### 1. Script de Backup Completo

```bash
#!/bin/bash
# backup_complete.sh

BACKUP_DIR="/opt/defisio/backups"
DATE=$(date +%Y%m%d_%H%M%S)

# Criar diretório de backup
mkdir -p $BACKUP_DIR/$DATE

# Backup do banco de dados
docker exec postgres-db-defisio pg_dump -U linfedemapp_prod linfedemapp_prod > $BACKUP_DIR/$DATE/database.sql

# Backup dos arquivos de configuração
cp -r /opt/defisio/config $BACKUP_DIR/$DATE/
cp .env.prod $BACKUP_DIR/$DATE/

# Compactar backup
tar -czf $BACKUP_DIR/defisio_backup_$DATE.tar.gz -C $BACKUP_DIR $DATE
rm -rf $BACKUP_DIR/$DATE

echo "✅ Backup completo criado: defisio_backup_$DATE.tar.gz"
```

### 2. Script de Restauração

```bash
#!/bin/bash
# restore.sh

if [ -z "$1" ]; then
    echo "Uso: $0 <arquivo_backup.tar.gz>"
    exit 1
fi

BACKUP_FILE=$1
RESTORE_DIR="/tmp/defisio_restore"

# Extrair backup
mkdir -p $RESTORE_DIR
tar -xzf $BACKUP_FILE -C $RESTORE_DIR

# Parar serviços
docker-compose -f docker-compose.prod.yml down

# Restaurar banco de dados
cat $RESTORE_DIR/*/database.sql | docker exec -i postgres-db-defisio psql -U linfedemapp_prod

# Iniciar serviços
docker-compose -f docker-compose.prod.yml up -d

echo "✅ Restauração concluída"
```

## Checklist de Deploy

- [ ] Configurar variáveis de ambiente de produção
- [ ] Configurar certificados SSL
- [ ] Configurar Nginx como proxy reverso
- [ ] Configurar firewall
- [ ] Testar conectividade do banco de dados
- [ ] Configurar backup automático
- [ ] Configurar monitoramento de logs
- [ ] Testar endpoints principais
- [ ] Configurar renovação automática de SSL
- [ ] Documentar credenciais de acesso
- [ ] Configurar alertas de monitoramento

## Solução de Problemas em Produção

### Logs Importantes

```bash
# Logs da aplicação
docker-compose -f docker-compose.prod.yml logs defisio-spring-app

# Logs do banco
docker-compose -f docker-compose.prod.yml logs postgres-db-defisio

# Logs do Nginx
sudo tail -f /var/log/nginx/defisio_error.log

# Logs do sistema
sudo journalctl -u docker
```

### Comandos de Emergência

```bash
# Reiniciar apenas a aplicação
docker-compose -f docker-compose.prod.yml restart defisio-spring-app

# Reiniciar todo o stack
docker-compose -f docker-compose.prod.yml restart

# Rollback rápido
docker-compose -f docker-compose.prod.yml down
docker-compose -f docker-compose.prod.yml up -d

# Verificar recursos
docker stats
df -h
free -h
```

Este guia deve cobrir a maioria dos cenários de deploy em produção. Para casos específicos, consulte a documentação do Spring Boot e Docker.