#!/bin/bash
set -e

mkdir -p /var/log/defisio

echo "=========================================================="
echo "  DEFISIO API - Inicializando..."
echo "=========================================================="

# Configurar ngrok authtoken
if [ -n "$NGROK_AUTHTOKEN" ]; then
    echo "[$(date)] Configurando ngrok authtoken..."
    ngrok config add-authtoken $NGROK_AUTHTOKEN
else
    echo "[$(date)] ERRO: NGROK_AUTHTOKEN não definido!"
    exit 1
fi

# Aguardar PostgreSQL (healthcheck do compose cuida, mas garantir)
echo "[$(date)] Aguardando PostgreSQL..."
sleep 5

# Iniciar a aplicação Java em background
echo "[$(date)] Iniciando aplicação Java..."
java -jar target/defisio-0.0.1-SNAPSHOT.jar &
JAVA_PID=$!

# Aguardar a aplicação iniciar
echo "[$(date)] Aguardando API subir..."
RETRIES=0
until curl -sf http://localhost:8083/actuator/health > /dev/null 2>&1 || [ $RETRIES -eq 60 ]; do
    RETRIES=$((RETRIES + 1))
    sleep 2
done

if ! kill -0 $JAVA_PID 2>/dev/null; then
    echo "[$(date)] ERRO: Aplicação Java não iniciou corretamente"
    exit 1
fi

echo "[$(date)] API rodando na porta 8083"

# Iniciar ngrok tunnel
echo "[$(date)] Iniciando ngrok tunnel na porta 8083..."
ngrok http 8083 --log=stdout --api-addr 0.0.0.0:4040 &
NGROK_PID=$!

# Aguardar ngrok e capturar URL
sleep 8
NGROK_URL=$(curl -sf http://localhost:4040/api/tunnels | grep -o '"public_url":"https://[^"]*' | grep -o 'https://[^"]*' | head -1 || true)

echo ""
echo "=========================================================="
echo "  DEFISIO API - ONLINE"
echo "=========================================================="
echo "  Local:  http://localhost:8083"
echo "  Ngrok:  ${NGROK_URL:-Aguardando...}"
echo "=========================================================="
echo ""

# Persistir URL no log
echo "[$(date)] API_LOCAL=http://localhost:8083" >> /var/log/defisio/urls.log
echo "[$(date)] API_NGROK_URL=${NGROK_URL}" >> /var/log/defisio/urls.log
echo "[$(date)] NGROK_DASHBOARD=http://localhost:4040" >> /var/log/defisio/urls.log

# Loop para manter logando a URL periodicamente (caso mude)
(
  while true; do
    sleep 60
    CURRENT_URL=$(curl -sf http://localhost:4040/api/tunnels | grep -o '"public_url":"https://[^"]*' | grep -o 'https://[^"]*' | head -1 || true)
    if [ -n "$CURRENT_URL" ]; then
      echo "[$(date)] API_NGROK_URL=$CURRENT_URL" >> /var/log/defisio/urls.log
    fi
  done
) &

# Manter o container rodando
wait $JAVA_PID