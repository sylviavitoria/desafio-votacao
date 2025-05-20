# Votação

## Objetivo

No cooperativismo, cada associado possui um voto e as decisões são tomadas em assembleias, por votação. Imagine que você deve criar uma solução para dispositivos móveis para gerenciar e participar dessas sessões de votação.
Essa solução deve ser executada na nuvem e promover as seguintes funcionalidades através de uma API REST:

- Cadastrar uma nova pauta
- Abrir uma sessão de votação em uma pauta (a sessão de votação deve ficar aberta por
  um tempo determinado na chamada de abertura ou 1 minuto por default)
- Receber votos dos associados em pautas (os votos são apenas 'Sim'/'Não'. Cada associado
  é identificado por um id único e pode votar apenas uma vez por pauta)
- Contabilizar os votos e dar o resultado da votação na pauta

Para fins de exercício, a segurança das interfaces pode ser abstraída e qualquer chamada para as interfaces pode ser considerada como autorizada. A solução deve ser construída em java, usando Spring-boot, mas os frameworks e bibliotecas são de livre escolha (desde que não infrinja direitos de uso).

É importante que as pautas e os votos sejam persistidos e que não sejam perdidos com o restart da aplicação.

O foco dessa avaliação é a comunicação entre o backend e o aplicativo mobile. Essa comunicação é feita através de mensagens no formato JSON, onde essas mensagens serão interpretadas pelo cliente para montar as telas onde o usuário vai interagir com o sistema. A aplicação cliente não faz parte da avaliação, apenas os componentes do servidor. O formato padrão dessas mensagens será detalhado no anexo 1.

## Como proceder

Por favor, **CLONE** o repositório e implemente sua solução, ao final, notifique a conclusão e envie o link do seu repositório clonado no GitHub, para que possamos analisar o código implementado.

Lembre de deixar todas as orientações necessárias para executar o seu código.

### Tarefas bônus

### Tarefa Bônus 3 - Versionamento da API

### Como você versionaria a API da sua aplicação? Que estratégia usar?
Estratégia atual no projeto utiliza versionamento via URI. A aplicação já utiliza uma abordagem de versionamento no caminho:
- `/api/v1/associados`
- `/api/v1/pautas`
- `/api/v1/sessoes`
- `/api/v1/votos`

## O que será analisado

- Simplicidade no design da solução (evitar over engineering)
- Organização do código
- Arquitetura do projeto
- Boas práticas de programação (manutenibilidade, legibilidade etc)
- Possíveis bugs
- Tratamento de erros e exceções
- Explicação breve do porquê das escolhas tomadas durante o desenvolvimento da solução
- Uso de testes automatizados e ferramentas de qualidade
- Limpeza do código
- Documentação do código e da API
- Logs da aplicação
- Mensagens e organização dos commits

## Dicas

- Teste bem sua solução, evite bugs
- Deixe o domínio das URLs de callback passiveis de alteração via configuração, para facilitar
  o teste tanto no emulador, quanto em dispositivos fisicos.
  Observações importantes
- Não inicie o teste sem sanar todas as dúvidas
- Iremos executar a aplicação para testá-la, cuide com qualquer dependência externa e
  deixe claro caso haja instruções especiais para execução do mesmo
  Classificação da informação: Uso Interno



### Tipo de tela – FORMULARIO

A tela do tipo FORMULARIO exibe uma coleção de campos (itens) e possui um ou dois botões de ação na parte inferior.

O aplicativo envia uma requisição POST para a url informada e com o body definido pelo objeto dentro de cada botão quando o mesmo é acionado. Nos casos onde temos campos de entrada
de dados na tela, os valores informados pelo usuário são adicionados ao corpo da requisição. Abaixo o exemplo da requisição que o aplicativo vai fazer quando o botão “Ação 1” for acionado:

```
POST http://seudominio.com/ACAO1
{
    “campo1”: “valor1”,
    “campo2”: 123,
    “idCampoTexto”: “Texto”,
    “idCampoNumerico: 999
    “idCampoData”: “01/01/2000”
}
```

Obs: o formato da url acima é meramente ilustrativo e não define qualquer padrão de formato.

### Tipo de tela – SELECAO

A tela do tipo SELECAO exibe uma lista de opções para que o usuário.

O aplicativo envia uma requisição POST para a url informada e com o body definido pelo objeto dentro de cada item da lista de seleção, quando o mesmo é acionado, semelhando ao funcionamento dos botões da tela FORMULARIO.

# desafio-votacao

# 🗳️ API de Votação


## ✅ Funcionalidades

### Associados

- Cadastro e listagem por id de associados  
- Atualização e exclusão de associados  
- Paginação e ordenação na listagem de associados  

### Pautas

- Cadastro de novas pautas com título, descrição e criador  
- Consulta de pauta por ID com status atualizado  
- Listagem de pautas com paginação e ordenação  
- Atualização de pautas existentes (apenas no status CRIADA)  
- Exclusão de pautas (quando não estão em votação)  
- Contagem automática de votos  

### Sessões de Votação

- Abertura de sessões de votação para pautas  
- Criação imediata ou agendamento para datas futuras  
- Configuração de duração personalizada ou padrão (1 minuto)  
- Consulta da sessão 
- Listagem de sessões com paginação e ordenação  
- Atualização do período de votação (extensão do prazo)  

### Votos

- Registro de votos de associados em pautas  
- Validação de sessão aberta e voto único por associado  
- Consulta de votos registrados por ID  
- Atualização de votos durante a sessão aberta  
- Consulta de resultado da votação 

### Recursos Adicionais

- Documentação completa com Swagger/OpenAPI  
- Gerenciamento de exceções com mensagens amigáveis  
- Validação de dados em todas as operações  
- Logs detalhados para auditoria e monitoramento  
- Containerização com Docker  
- Migração de banco de dados com Flyway  
- Perfis configuráveis para diferentes ambientes

---

##  Tecnologias utilizadas

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
### Criar um associado
- **POST** `http://localhost:8080/api/v1/associados` 
```json
{
    "nome": "João Silva",
    "cpf": "15345534769",
    "email": "joao.silva@email.com"
}
```
### Buscar associado por ID
**GET** `http://localhost:8080/api/v1/associados/{id}` 
### Listar associados (com paginação e ordenação)
**GET** `http://localhost:8080/api/v1/associados`
###  Atualizar um associado
**PUT**  `http://localhost:8080/api/v1/associados/{id}` 
```json
  {
    "nome": "Silva",
    "cpf": "12345678899",
    "email": "silva.silva@email.com"
  }
```
### Excluir um associado
- **DELETE** `http://localhost:8080/api/v1/associados/{id}` 

---

## 🔹 Pautas

###  Criar uma pauta  
**POST** `http://localhost:8080/api/v1/pautas`

```json
{
    "titulo": "Assembleia Geral 2025",
    "descricao": "Discussão sobre os resultados financeiros de 2024",
    "criadorId": 1
}
```

###  Buscar pauta por ID  
**GET** `http://localhost:8080/api/v1/pautas/{id}`

###  Listar pautas (com paginação e ordenação)  
**GET** `http://localhost:8080/api/v1/pautas`

###  Atualizar uma pauta  
**PUT** `http://localhost:8080/api/v1/pautas/{id}`

```json
{
    "titulo": "Assembleia Extraordinária 2025",
    "descricao": "Revisão dos resultados financeiros do primeiro trimestre"
}
```

###  Excluir uma pauta  
**DELETE** `http://localhost:8080/api/v1/pautas/{id}`

---

## 🔹 Sessões de Votação

###  Criar uma sessão de votação  
**POST** `http://localhost:8080/api/v1/sessoes`

#### Abertura imediata:

```json
{
    "pautaId": 1,
    "duracaoMinutos": 5
}
```

#### Agendamento:

```json
{
    "pautaId": 1,
    "dataInicio": "2025-05-25T22:00:00",
    "dataFim": "2025-05-26T11:00:00"
}
```

#### Default:

```json
{
    "pautaId": 1
}
```

###  Consultar status da sessão de votação  
**GET** `http://localhost:8080/api/v1/sessoes/{id}`

###  Listar sessões (com paginação e ordenação)  
**GET** `http://localhost:8080/api/v1/sessoes`

###  Atualizar período da sessão  
**PUT** `http://localhost:8080/api/v1/sessoes/{id}/periodo`

#### Adicionar minutos:

```json
{
    "minutosAdicionais": 30
}
```

#### Nova data fim:

```json
{
    "dataFim": "2025-05-27T00:00:00"
}
```

---

## 🔹 Votos

###  Registrar voto  
**POST** `http://localhost:8080/api/v1/votos`

```json
{
    "associadoId": 1,
    "pautaId": 1,
    "opcao": "SIM"
}
```

###  Buscar voto por ID  
**GET** `http://localhost:8080/api/v1/votos/{id}`

###  Atualizar voto (apenas durante sessão aberta)  
**PUT** `http://localhost:8080/api/v1/votos/{id}`

```json
{
    "opcao": "NAO"
}
```
**GET** `http://localhost:8080/api/v1/votos/pautas/{pautaId}/resultado`

---

##  Parâmetros Comuns para Endpoints de Listagem

Os endpoints de listagem (`GET /api/v1/associados`, `/api/v1/pautas`, `/api/v1/sessoes`) aceitam os seguintes parâmetros de consulta:

- `page` (padrão: 0): Número da página para paginação  
- `size` (padrão: 10): Número de itens por página  
- `sort` (opcional): Campo para ordenação (ex: `nome`, `titulo`, `dataAbertura`)

#  Estrutura Completa do Sistema de Votação

---

## 1.  Domínio e Modelagem

### 1.1.  Associado
**Entidade:** Representa os membros com direito a voto  
**Atributos principais:** `id`, `nome`, `cpf`, `email`  

**Responsabilidades:**
- Identificação única dos votantes (CPF)
- Rastreabilidade nas votações

A entidade separada para garantir unicidade e permitir auditoria de votos.
- Regra de Negócio Importante:
  - Não é possível excluir um associado que está vinculado a uma pauta como criador
  - O CPF deve ser único no sistema

####  Service:
- **Criar:** Valida CPF único, usa Mapper para transformar DTO em entidade e retorna DTO de resposta.
- **Buscar por ID:** Retorna associado por ID, lança exceção se não existir.
- **Listar:** Paginação e ordenação para facilitar consultas.
- **Atualizar:** Atualiza dados, valida existência.
- **Deletar:** Só permite se não houver pautas criadas pelo associado.

####  DTOs:
- `AssociadoRequest` dados que o associado mandou o request.
- `AssociadoResponse` seria os dados que seram enviados para o associado, como forma se ocutar dados sensiveis.
- `AssociadoListarResponse` seria os dados que seram enviados para o associado, como forma se ocutar dados sensiveis.
- `AssociadoDTO` (para relacionamentos)

---

### 1.2.  Pauta
**Entidade:** Representa os temas em deliberação  
**Atributos principais:** `id`, `titulo`, `descricao`, `status`, `criador`  
**Estados:** `CRIADA → EM_VOTACAO → APROVADA/RECUSADA/EMPATADA`  

#### Relacionamentos:
- `ManyToOne` com `Associado` - Uma pauta é criada por um associado, e um associado pode criar múltiplas pautas
 
A entidade central, com ciclo de vida bem definido para facilitar auditoria e transparência.

####  Service:
- **Criar:** Valida existência do criador, status inicial CRIADA. Ela incia como criada como forma de que as as pautas são criadas quando o associado cadastra, e com base no iniciar sessão ele muda de fluxo. `CRIADA → EM_VOTACAO → APROVADA/RECUSADA/EMPATADA` para mostrar o resultado do status e dos votos.
- **Buscar por ID:** Atualiza status automaticamente conforme regras de negócio.
- **Listar:** Paginação, ordenação e atualização de status em lote.
- **Atualizar:** Só permite se status for CRIADA.
- **Deletar:** Não permite se estiver em votação.

- Regras de Negócio Importantes: 
  - Uma pauta só pode ser atualizada se estiver no status CRIADA
  - Não é possível excluir uma pauta que já está em votação (EM_VOTACAO)
  - Ao criar uma sessão para uma pauta, seu status é automaticamente alterado para EM_VOTACAO
  - Uma pauta só pode ter uma única sessão de votação vinculada a ela

####  Método central:
- `verificarEAtualizarStatusPauta`: Atualiza status da pauta conforme votos ao final da sessão.

####  DTOs:
- `PautaRequest` dados que o associado vai enviar a requisição. 
- `PautaResponse` seria os dados que seram enviados para o usuario, com forma de segurança de dados
- `PautaAtualizarRequest` irá atualizar somente dados que foram enviados pelo usuario.

---

### 1.3.  Sessão de Votação
**Entidade:** Gerencia o período de votação  
**Atributos principais:** `id`, `dataAbertura`, `dataFechamento`, `status`, `pauta`  
**Estados:** `FECHADA → ABERTA → FINALIZADA`  

Separada da pauta para permitir agendamento e inicio de sessão imediata e controle de períodos.

#### Relacionamentos:
- `OneToOne` com `Pauta` - Uma sessão pertence exclusivamente a uma pauta e uma pauta só pode ter uma sessão de votação

####  Service:
- **Criar:** Dois modos (imediato ou agendado), validações de datas, status inicial conforme contexto. Esses dois modos de imediato ou agendado fazem com que o associado tenha maior controle para agendar uma pauta em tempos diferentes.
- **Buscar/Listar:** Atualiza status da sessão e pauta conforme o tempo.
- **Atualizar Período:** Permite extensão do período, com validações.
  
- Regras de Negócio Importantes:
  - Se nenhuma duração for especificada, a sessão terá duração padrão de 1 minuto
  - O período de votação só pode ser estendido, nunca reduzido
  - Não é possível estender uma sessão que já foi finalizada
  - A data de início deve ser anterior à data de fim

####  Método central:
- `configurarStatusSessao`: Atualiza status da sessão e pauta, contabiliza votos ao finalizar. Seria usado como metodo central para que seja alterado de forma simples em um método.

### Métodos auxiliares importantes:
- `estaAberta()`: Verifica se a sessão está no período de votação
- `deveSerFinalizada()`: Verifica se a sessão precisa ser finalizada

####  DTOs:
- `SessaoVotacaoRequest` seram os dados enviados pelos associados.
- `SessaoVotacaoResponse` será os dados que serão enviados para o associados.
- `SessaoVotacaoAtualizarRequest` seram informados os dados que seram atualizados.

---

### 1.4.  Voto
**Entidade:** Registra as decisões dos associados  
**Atributos principais:** `id`, `associado`, `pauta`, `opcao (SIM/NÃO)`, `dataHora`  

####  Regras:
- Um voto por associado por pauta (constraint de unicidade no banco)

####  Relacionamentos:
- `ManyToOne` com `Associado` Associado tem várias pautas, e so pode ter somente um voto.
- `ManyToOne` com `Pauta` Voto tem várias pautas, e Voto so pode ter somente uma pauta.

####  Enum:
- `OpcaoVoto` (`SIM` / `NAO`) – Garante integridade dos dados

---

## 2.  Serviços e Métodos

### 2.1. VotoService

####  registrarVoto(VotoRequest request)
- Valida existência de associado, pauta e sessão aberta
- Garante unicidade do voto
- Salva e retorna DTO  
Isso evita votos duplicados e só permite votos em sessões válidas

####  consultarResultado(Long pautaId)
- Busca pauta
- Conta votos `SIM` e `NÃO`
- Retorna DTO com totais  
Faz com que a consulta seja eficiente com resposta clara para o usuário

####  atualizarVoto(Long id, VotoAtualizarRequest request)
- Permite alteração apenas durante sessão aberta
- Valida existência do voto e sessão
- Atualiza apenas a opção  
Dá flexibilidade ao usuário, com restrição temporal

####  buscarPorId(Long id)
- Retorna o voto pelo ID  
Permite auditoria e rastreabilidade

---

## 3.  DTOs e Mapper

- `VotoRequest`: Para registrar voto (entrada)  
- `VotoAtualizarRequest`: Para atualizar voto (entrada)  
- `VotoResponse`: Para resposta detalhada (saída)  
- `ResultadoVotacaoResponse`: Para resultado da votação (saída)  
- `VotoMapper`: Usa MapStruct para transformar entidades em DTOs

---

## 4.  Repositório

- `existsByAssociadoIdAndPautaId`: Garante unicidade do voto  
- `countByPautaIdAndOpcao`: Otimiza contagem de votos  
- `findByPautaId`: Recupera todos os votos de uma pauta  

---

## 5. Justificativas das Escolhas

- Entidade independente facilita auditoria e evolução futura
- Validações em camadas garantem integridade
- Transações asseguram atomicidade
- Uso de DTOs separa entrada/saída claramente
- MapStruct reduz código repetitivo
- Logs aumentam a rastreabilidade

---

## 6.  Aspectos Técnicos Adicionais

### 6.1.  Gerenciamento de Exceções
- ExceptionHandler personalizado
- Exceções customizadas (`BusinessException`, `EntityNotFoundException`)
- Logs detalhados

### 6.2.  Documentação com OpenAPI/Swagger
- Exemplos de requisições
- Descrições completas de parâmetros e respostas
- Media types padronizados (`application/json`)

### 6.3.  Configurações de Banco de Dados
- Migrations com Flyway
- Constraints para garantir integridade
- Índices otimizando performance
- Profiles para diferentes ambientes (`h2`, `postgres`)

### 6.4.  Containerização
- `docker-compose` para facilitar testes e implantação
- Variáveis de ambiente via `.env`

### 6.5.  Testes
- Unitários para regras de negócio
- Mocking para isolamento
- Parametrização para múltiplos cenários
- Nomes descritivos facilitando entendimento

### 6.6.  Segurança
- Bean Validation para entradas
- Validações em múltiplas camadas


### 6.7. Monitoramento e Observabilidade
- Logs estratégicos em operações críticas


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
https://github.com/sylviavitoria/desafio-votacao
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
