# Exemplos de Uso da API DEFISIO

Este arquivo contém exemplos práticos de como usar a API DEFISIO em diferentes cenários.

## Configuração Inicial

### 1. Variáveis de Ambiente (Para Scripts/Postman)

```bash
# Base URL da API
BASE_URL=http://localhost:8083

# Após fazer login, armazene o token
TOKEN=seu_token_jwt_aqui
```

## Fluxo Básico de Uso

### 1. Registro e Login

#### Registrar um novo usuário (fisioterapeuta)
```bash
curl -X POST "${BASE_URL}/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Dr. Ana Santos",
    "idade": "32",
    "origem": "UFPE",
    "email": "ana.santos@hospital.com",
    "telefone": "+55 81 98765-4321",
    "titulacao": "Especialista em Fisioterapia Oncológica",
    "password": "minhasenha123"
  }'
```

#### Fazer login
```bash
curl -X POST "${BASE_URL}/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ana.santos@hospital.com",
    "password": "minhasenha123"
  }'
```

**Resposta esperada:**
```json
{
  "email": "ana.santos@hospital.com",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJsb2dpbi1hdXRoLWFwaSIsInN1YiI6ImFuYS5zYW50b3NAaG9zcGl0YWwuY29tIiwiaWQiOiIxMjNlNDU2Ny1lODliLTEyZDMtYTQ1Ni00MjY2MTQxNzQwMDAiLCJuYW1lIjoiRHIuIEFuYSBTYW50b3MiLCJpZGFkZSI6IjMyIiwidGVsZWZvbmUiOiIrNTUgODEgOTg3NjUtNDMyMSIsIm9yaWdlbSI6IlVGUEUiLCJ0aXR1bGFjYW8iOiJFc3BlY2lhbGlzdGEgZW0gRmlzaW90ZXJhcGlhIE9uY29sw7NnaWNhIiwiZXhwIjoxNzA5NTUyNDAwfQ..."
}
```

### 2. Verificar Perfil do Usuário

```bash
curl -X GET "${BASE_URL}/auth/me" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 3. Gerenciamento de Pacientes

#### Adicionar novo paciente
```bash
curl -X POST "${BASE_URL}/api/pacientes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN}" \
  -d '{
    "usuarioId": "123e4567-e89b-12d3-a456-426614174000",
    "nome": "Maria Oliveira",
    "dataNascimento": "1975-03-22",
    "endereco": "Av. Boa Viagem, 500, Recife-PE",
    "telefone": "+55 81 91234-5678",
    "pesoCorporal": "68.0",
    "altura": "1.62",
    "nivelAtividadeFisica": "Moderado",
    "estadoCivil": "Viúva",
    "ocupacao": "Contadora",
    "dataDiagnostiCancer": "2023-08-15",
    "procedimentos": [
      "Mastectomia parcial",
      "Linfonodos sentinela"
    ],
    "alteracoesCutaneas": [
      "Cicatriz pós-cirúrgica",
      "Leve edema"
    ],
    "queixasMusculoesqueleticas": "Rigidez no ombro direito, limitação de movimento",
    "sintomasLinfedema": "Sensação de peso no braço direito",
    "sinalCacifo": "Leve",
    "sinalCascaLaranja": "Ausente",
    "sinalStemmer": "Negativo",
    "radioterapia": {
      "tipo": "Radioterapia externa conformacional",
      "duracao": "5 semanas"
    },
    "cirurgia": {
      "tipo": "Quadrantectomia",
      "duracao": "2 horas"
    },
    "disseccaoAxilar": {
      "tipo": "Biópsia do linfonodo sentinela",
      "duracao": "30 minutos"
    },
    "hormonoterapia": {
      "tipo": "Anastrozol",
      "duracao": "5 anos"
    },
    "detalhesHormonoterapia": "Anastrozol 1mg/dia, iniciado 3 meses pós-cirurgia",
    "quimioterapia": {
      "tipo": "Não realizada",
      "duracao": "N/A"
    },
    "observacaoPaciente": "Paciente muito colaborativa, boa aderência. Apresenta ansiedade relacionada ao prognóstico."
  }'
```

#### Listar pacientes do usuário
```bash
curl -X GET "${BASE_URL}/api/pacientes/usuario/123e4567-e89b-12d3-a456-426614174000" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 4. Adicionar Medições

#### Primeira medição completa
```bash
curl -X POST "${BASE_URL}/api/pacientes/456e7890-e89b-12d3-a456-426614174001/mensuracao" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN}" \
  -d '{
    "tipoReferencia": "Braço esquerdo (não afetado)",
    "observacaoMedicao": "Primeira avaliação - paciente calma, ambiente controlado, medições realizadas em triplicata",
    "volumetry": {
      "referenceArm": "Esquerdo",
      "affectedArm": "Direito",
      "volumesReferencia": [245.2, 278.8, 305.1],
      "volumesAfetado": [268.5, 315.3, 347.9],
      "volumeDifference": 12.8
    },
    "perimetry": {
      "pontosRef": "5cm, 10cm, 15cm acima do epicôndilo lateral",
      "leftArmInputs": ["24.5", "27.2", "29.8"],
      "rightArmInputs": ["26.8", "30.1", "33.2"],
      "leftArmComprimento": "43cm",
      "rightArmComprimento": "43cm",
      "differences": [2.3, 2.9, 3.4]
    }
  }'
```

#### Medição de acompanhamento (1 mês depois)
```bash
curl -X POST "${BASE_URL}/api/pacientes/456e7890-e89b-12d3-a456-426614174001/mensuracao" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN}" \
  -d '{
    "tipoReferencia": "Braço esquerdo (não afetado)",
    "observacaoMedicao": "Acompanhamento 1 mês - paciente relata melhora após fisioterapia",
    "volumetry": {
      "referenceArm": "Esquerdo",
      "affectedArm": "Direito",
      "volumesReferencia": [244.8, 278.1, 304.5],
      "volumesAfetado": [261.2, 308.7, 338.9],
      "volumeDifference": 10.1
    },
    "perimetry": {
      "pontosRef": "5cm, 10cm, 15cm acima do epicôndilo lateral",
      "leftArmInputs": ["24.3", "27.0", "29.6"],
      "rightArmInputs": ["25.9", "29.2", "31.8"],
      "leftArmComprimento": "43cm",
      "rightArmComprimento": "43cm",
      "differences": [1.6, 2.2, 2.2]
    }
  }'
```

#### Consultar histórico de medições
```bash
curl -X GET "${BASE_URL}/api/pacientes/usuario/123e4567-e89b-12d3-a456-426614174000/456e7890-e89b-12d3-a456-426614174001/mensuracoes" \
  -H "Authorization: Bearer ${TOKEN}"
```

## Cenários de Uso Avançados

### 1. Buscar paciente específico para consulta detalhada
```bash
curl -X GET "${BASE_URL}/api/pacientes/456e7890-e89b-12d3-a456-426614174001" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 2. Atualizar dados de um paciente (seria necessário endpoint PUT - não implementado ainda)
```bash
# Este endpoint ainda não existe, mas seria assim:
# curl -X PUT "${BASE_URL}/api/pacientes/456e7890-e89b-12d3-a456-426614174001" \
#   -H "Content-Type: application/json" \
#   -H "Authorization: Bearer ${TOKEN}" \
#   -d '{ "telefone": "+55 81 99999-8888" }'
```

### 3. Remover paciente (com cuidado!)
```bash
curl -X DELETE "${BASE_URL}/api/pacientes/456e7890-e89b-12d3-a456-426614174001" \
  -H "Authorization: Bearer ${TOKEN}"
```

## Scripts de Teste Automatizado

### Script Bash para teste completo
```bash
#!/bin/bash

BASE_URL="http://localhost:8083"
EMAIL="teste.$(date +%s)@exemplo.com"
PASSWORD="senha123"

echo "🚀 Iniciando teste da API DEFISIO..."

# 1. Registrar usuário
echo "📝 Registrando usuário..."
RESPONSE=$(curl -s -X POST "${BASE_URL}/auth/register" \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"Dr. Teste\",
    \"idade\": \"30\",
    \"origem\": \"Teste\",
    \"email\": \"${EMAIL}\",
    \"telefone\": \"+55 81 99999-9999\",
    \"titulacao\": \"Fisioterapeuta\",
    \"password\": \"${PASSWORD}\"
  }")

TOKEN=$(echo $RESPONSE | jq -r '.token')
USER_ID=$(echo $RESPONSE | jq -r '.token' | cut -d'.' -f2 | base64 -d 2>/dev/null | jq -r '.id' 2>/dev/null || echo "USER_ID_PLACEHOLDER")

echo "✅ Usuário registrado. Token obtido."

# 2. Verificar perfil
echo "👤 Verificando perfil..."
curl -s -X GET "${BASE_URL}/auth/me" \
  -H "Authorization: Bearer ${TOKEN}" | jq '.'

# 3. Adicionar paciente
echo "🏥 Adicionando paciente..."
PATIENT_RESPONSE=$(curl -s -X POST "${BASE_URL}/api/pacientes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN}" \
  -d "{
    \"usuarioId\": \"${USER_ID}\",
    \"nome\": \"Paciente Teste\",
    \"dataNascimento\": \"1980-01-01\",
    \"endereco\": \"Endereço Teste\",
    \"telefone\": \"+55 81 88888-8888\",
    \"pesoCorporal\": \"70.0\",
    \"altura\": \"1.70\"
  }")

PATIENT_ID=$(echo $PATIENT_RESPONSE | jq -r '.id')
echo "✅ Paciente adicionado. ID: ${PATIENT_ID}"

# 4. Adicionar medição
echo "📏 Adicionando medição..."
curl -s -X POST "${BASE_URL}/api/pacientes/${PATIENT_ID}/mensuracao" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN}" \
  -d '{
    "tipoReferencia": "Teste",
    "observacaoMedicao": "Medição de teste",
    "volumetry": {
      "referenceArm": "Esquerdo",
      "affectedArm": "Direito",
      "volumesReferencia": [250.0],
      "volumesAfetado": [260.0],
      "volumeDifference": 4.0
    },
    "perimetry": {
      "pontosRef": "Teste",
      "leftArmInputs": ["25"],
      "rightArmInputs": ["26"],
      "leftArmComprimento": "40cm",
      "rightArmComprimento": "40cm",
      "differences": [1.0]
    }
  }' | jq '.'

echo "✅ Medição adicionada com sucesso!"

# 5. Listar pacientes
echo "📋 Listando pacientes..."
curl -s -X GET "${BASE_URL}/api/pacientes/usuario/${USER_ID}" \
  -H "Authorization: Bearer ${TOKEN}" | jq '.'

echo "🎉 Teste completo realizado com sucesso!"
```

### Python Script para integração
```python
import requests
import json
from datetime import datetime

class DefisioAPI:
    def __init__(self, base_url="http://localhost:8083"):
        self.base_url = base_url
        self.token = None
        self.user_id = None
    
    def register(self, user_data):
        """Registra novo usuário"""
        response = requests.post(
            f"{self.base_url}/auth/register",
            json=user_data
        )
        if response.status_code == 200:
            data = response.json()
            self.token = data['token']
            return data
        return None
    
    def login(self, email, password):
        """Faz login e obtém token"""
        response = requests.post(
            f"{self.base_url}/auth/login",
            json={"email": email, "password": password}
        )
        if response.status_code == 200:
            data = response.json()
            self.token = data['token']
            return data
        return None
    
    def get_headers(self):
        """Retorna headers com autorização"""
        return {"Authorization": f"Bearer {self.token}"}
    
    def get_profile(self):
        """Obtém perfil do usuário logado"""
        response = requests.get(
            f"{self.base_url}/auth/me",
            headers=self.get_headers()
        )
        if response.status_code == 200:
            data = response.json()
            self.user_id = data['id']
            return data
        return None
    
    def add_patient(self, patient_data):
        """Adiciona novo paciente"""
        patient_data['usuarioId'] = self.user_id
        response = requests.post(
            f"{self.base_url}/api/pacientes",
            json=patient_data,
            headers=self.get_headers()
        )
        return response.json() if response.status_code == 200 else None
    
    def add_measurement(self, patient_id, measurement_data):
        """Adiciona medição para paciente"""
        response = requests.post(
            f"{self.base_url}/api/pacientes/{patient_id}/mensuracao",
            json=measurement_data,
            headers=self.get_headers()
        )
        return response.json() if response.status_code == 200 else None

# Exemplo de uso
if __name__ == "__main__":
    api = DefisioAPI()
    
    # Registrar ou fazer login
    user_data = {
        "name": "Dr. Python Test",
        "idade": "35",
        "origem": "API Test",
        "email": f"python.test.{datetime.now().timestamp()}@exemplo.com",
        "telefone": "+55 81 99999-9999",
        "titulacao": "Fisioterapeuta",
        "password": "senha123"
    }
    
    result = api.register(user_data)
    if result:
        print(f"✅ Usuário registrado: {result['email']}")
        
        profile = api.get_profile()
        print(f"👤 Perfil obtido: {profile['name']}")
        
        # Adicionar paciente
        patient = {
            "nome": "Paciente Python Test",
            "dataNascimento": "1985-05-15",
            "telefone": "+55 81 88888-8888"
        }
        
        patient_result = api.add_patient(patient)
        if patient_result:
            print(f"🏥 Paciente adicionado: {patient_result['nome']}")
        
    else:
        print("❌ Falha no registro do usuário")
```

## Troubleshooting - Erros Comuns

### 1. Token expirado (401 Unauthorized)
```bash
# Faça login novamente para obter novo token
curl -X POST "${BASE_URL}/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email": "seu@email.com", "password": "suasenha"}'
```

### 2. Usuário não encontrado (400 Bad Request no login)
```bash
# Verifique se o email está correto ou registre novamente
curl -X POST "${BASE_URL}/auth/register" -H "Content-Type: application/json" -d '{...}'
```

### 3. Paciente não encontrado (404 Not Found)
```bash
# Verifique se o ID do paciente está correto
curl -X GET "${BASE_URL}/api/pacientes/usuario/SEU_USER_ID" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 4. Dados inválidos (400 Bad Request)
```bash
# Verifique se todos os campos obrigatórios estão presentes
# Verifique se os UUIDs estão no formato correto
# Verifique se os tipos de dados estão corretos (strings, numbers, arrays)
```

## Performance e Monitoramento

### Verificar saúde da API
```bash
# Check básico
curl -I "${BASE_URL}/auth/me"

# Tempo de resposta
time curl -s "${BASE_URL}/auth/me" > /dev/null
```

### Monitorar logs
```bash
# Logs do Docker
docker-compose logs -f defisio-spring-app

# Logs específicos de erro
docker-compose logs defisio-spring-app | grep ERROR
```

Esta documentação de exemplos deve cobrir a maioria dos casos de uso da API DEFISIO. Para casos específicos não cobertos aqui, consulte a documentação principal no README.md.