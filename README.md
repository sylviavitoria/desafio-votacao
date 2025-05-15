# 🗳️ API de Votação


## ✅ Funcionalidades

- Cadastro e listagem por id de **associados**
- Atualização e exclusão de associados
- Paginação e ordenação na listagem de associados
- Documentação com Swagger

---

## 🔧 Tecnologias utilizadas

- Java 21+
- Spring Boot
- Maven
- Docker
- Jakarta Validation
- Swagger (OpenAPI)
- Lombok
- MapStruct
- FlyWay

---

## 📂 Estrutura dos Endpoints

### 🔹 Associados

- `POST /api/v1/associados` – Criar um associado
```
{
    "nome": "João Silva",
    "cpf": "12345678781",
    "email": "joao.silva@email.com"
}
```
- `GET /api/v1/associados/{id}` – Buscar associado por ID
- `GET /api/v1/associados` – Listar associados (com paginação e ordenação)
- `PUT /api/v1/associados/{id}` – Atualizar um associado
```
  {
    "nome": "Silva",
    "cpf": "12345678899",
    "email": "silva.silva@email.com"
  }
```

- `DELETE /api/v1/associados/{id}` – Excluir um associado

---


### 👁️ Configuração `.env`
Para facilitar a configuração do banco de dados e evitar informações sensíveis no código, crie um arquivo .env na raiz do projeto com o seguinte conteúdo:
```bash
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_DB=apivotacao
POSTGRES_PORT=5432

SPRING_PROFILES_ACTIVE=postgres
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/${POSTGRES_DB}
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

PGADMIN_DEFAULT_EMAIL=admin@example.com
PGADMIN_DEFAULT_PASSWORD=admin
PGADMIN_PORT=5050
```

Esse arquivo .env é utilizado para centralizar as variáveis de ambiente da aplicação, deixando sua configuração mais simples e segura, especialmente quando for rodar via Docker ou em ambientes diferentes.

🐳 Observação:
- O Docker Compose utiliza essas variáveis para subir os containers do PostgreSQL e PgAdmin com as credenciais corretas.
- O Spring Boot também lerá essas variáveis ao inicializar, garantindo que a conexão com o banco seja automática conforme o ambiente.

---

# 🌬️ Como Executar

## Pré-requisitos
- Java 21+
- Maven
- Docker
- PostgreSQL ou H2 Database

## Passos para Execução

### 1. Clone o repositório
```bash
https://github.com/sylviavitoria/desafio-votacao/tree/dev
```
- Caso precise vá para a branch dev com no seu terminal da aplicação:
```bash
git checkout dev
```

### 2. Configure o ambiente
Ajuste as configurações de banco de dados em um dos arquivos:
- Configure suas credencias citado anteriormente e defina variáveis no arquivo `.env`

### 3. Execute a aplicação

**Opção 1: Via Maven**
```bash
mvn spring-boot:run
```

**Opção 2: Via Docker**
```bash
docker compose up --build
```

## 4. Acesse a Aplicação

### Documentação da API
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`

### Teste dos Endpoints
- **Postman:**  Adicione os Endpoints da API mencionados anteriormente `http://localhost:8080/api/v1/...`

### Gerenciamento do Banco de Dados
- **H2 Database** (para ambiente de desenvolvimento):
  - Console: `http://localhost:8080/h2-console`

- **PostgreSQL** (recomendado para ambiente Docker):
  - **pgAdmin:** `http://localhost:5050`
  - Credenciais de login: Definidas no arquivo `.env`

