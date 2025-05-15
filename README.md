# 🗳️ API de Votação


## ✅ Funcionalidades

- Cadastro, listagem e exclusão de **associados**
- Atualização e exclusão de associados
- Paginação e ordenação na listagem de associados
- Documentação com Swagger (OpenAPI 3)

---

## 🔧 Tecnologias utilizadas

- Java 21+
- Spring Boot
- Maven
- Docker
- Jakarta Validation
- Swagger (OpenAPI)
- Lombok

---

## 🚀 Como rodar o projeto

### 💻 Pré-requisitos

- Java 21 ou superior
- Maven 
- Docker (opcional)

### 🔄 Clonando o repositório

```bash
https://github.com/sylviavitoria/desafio-votacao.git
```

---

### ▶️ Rodando com Maven

```bash
./mvnw spring-boot:run
```

A aplicação será iniciada em: `http://localhost:8080`

---

### 🐳 Rodando com Docker

> Certifique-se de que o Docker esteja instalado e em execução.

1. *Rodar Docker:**

A aplicação ficará acessível em `http://localhost:8080`

---

## 📘 Documentação da API

Acesse a documentação interativa gerada com Swagger em:

```
http://localhost:8080/swagger-ui.html
```

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

