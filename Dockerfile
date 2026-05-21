FROM eclipse-temurin:21-jdk-jammy

# Instalar wget, unzip e curl para baixar o ngrok
RUN apt-get update && apt-get install -y wget unzip curl && rm -rf /var/lib/apt/lists/*

# Baixar e instalar ngrok
RUN wget https://bin.equinox.io/c/bNyj1mQVY4c/ngrok-v3-stable-linux-amd64.tgz && \
    tar -xvzf ngrok-v3-stable-linux-amd64.tgz && \
    mv ngrok /usr/local/bin/ && \
    rm ngrok-v3-stable-linux-amd64.tgz

WORKDIR /app

# Copiar código fonte
COPY . .

# Dar permissão e buildar
RUN chmod +x mvnw
RUN ./mvnw clean install -DskipTests

# Variável de ambiente para ngrok authtoken
ENV NGROK_AUTHTOKEN=""

# Expor portas
EXPOSE 8083 4040

# Copiar script de inicialização
COPY start.sh /app/start.sh
RUN chmod +x /app/start.sh

ENTRYPOINT ["/app/start.sh"]