docker-compose down

docker build -t defisio:latest ./

docker-compose up --build --force-recreate --remove-orphans