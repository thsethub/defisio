#!/bin/bash
set -e

mkdir -p /var/log/defisio

echo "=========================================================="
echo "  DEFISIO API - Inicializando..."
echo "=========================================================="

if [ -n "$NGROK_AUTHTOKEN" ]; then
    echo "[$(date)] Configurando ngrok authtoken..."
    ngrok config add-authtoken $NGROK_AUTHTOKEN
else
    echo "[$(date)] ERRO: NGROK_AUTHTOKEN não definido!"
    exit 1
fi

echo "[$(date)] Iniciando aplicação Java..."
java -jar target/defisio-0.0.1-SNAPSHOT.jar &
JAVA_PID=$!

echo "[$(date)] Aguardando API subir..."
RETRIES=0
until curl -sf http://localhost:8083/actuator/health > /dev/null 2>&1 || [ $RETRIES -eq 30 ]; do
    RETRIES=$((RETRIES + 1))
    sleep 2
done

if ! kill -0 $JAVA_PID 2>/dev/null; then
    echo "[$(date)] ERRO: Aplicação Java não iniciou corretamente"
    exit 1
fi

echo "[$(date)] API rodando na porta 8083"

echo "[$(date)] Iniciando ngrok tunnel na porta 8083..."
ngrok http 8083 --log=stdout &

echo "[$(date)] Aguardando URL do ngrok..."
RETRIES=0
NGROK_URL=""
until [ -n "$NGROK_URL" ] || [ $RETRIES -eq 15 ]; do
    NGROK_URL=$(curl -sf http://localhost:4040/api/tunnels | grep -o '"public_url":"https://[^"]*' | grep -o 'https://[^"]*' | head -1 || true)
    if [ -z "$NGROK_URL" ]; then
        RETRIES=$((RETRIES + 1))
        sleep 2
    fi
done

echo ""
echo "=========================================================="
echo "  DEFISIO API - ONLINE"
echo "=========================================================="
echo "  Local:  http://localhost:8083"
echo "  Ngrok:  ${NGROK_URL:-Aguardando...}"
echo "=========================================================="
echo ""

echo "[$(date)] API_LOCAL=http://localhost:8083" >> /var/log/defisio/urls.log
echo "[$(date)] API_NGROK_URL=${NGROK_URL}" >> /var/log/defisio/urls.log

(
  while true; do
    sleep 60
    CURRENT_URL=$(curl -sf http://localhost:4040/api/tunnels | grep -o '"public_url":"https://[^"]*' | grep -o 'https://[^"]*' | head -1 || true)
    if [ -n "$CURRENT_URL" ]; then
      echo "[$(date)] API_NGROK_URL=$CURRENT_URL" >> /var/log/defisio/urls.log
    fi
  done
) &

wait $JAVA_PID
