# 🗓️ Daily Planner API

Sistema de planejamento de tarefas diárias desenvolvido com **Spring Boot** e **PostgreSQL**, com CRUD completo de tarefas, categorias e um modelo de conclusão baseado em ocorrências.

Projeto de portfólio, desenvolvido com foco em boas práticas de arquitetura backend: separação em camadas, tratamento de exceções centralizado, DTOs e validação de dados.

Frontend deste projeto: [daily-planner-web](https://github.com/JotaGeVM/daily-planner-web)

---

## 🚀 Tecnologias utilizadas

- **Java 25**
- **Spring Boot 4.1**
  - Spring Web
  - Spring Data JPA
  - Bean Validation
- **PostgreSQL**
- **Maven** (com Maven Toolchains, para build reprodutível independente do `JAVA_HOME` da máquina)
- **Lombok**
- **H2** (banco em memória para testes)

---

## ✨ Funcionalidades

- CRUD completo de **Tarefas**, com suporte a recorrência (`NENHUMA`, `DIARIA`, `SEMANAL`, `QUINZENAL`, `MENSAL`), tipo (`EVENTO` ou `TAREFA`), dias da semana, horário e duração
- CRUD completo de **Categorias**, com cor de identificação (hexadecimal) e descrição opcional
- Uma categoria não pode ser removida enquanto houver tarefas vinculadas a ela
- **Ocorrências**: registro de conclusão de uma tarefa em um momento específico (`dataHora`). A existência do registro já representa a conclusão — não há um campo de status booleano separado
- Validação de dados de entrada (Bean Validation)
- Tratamento de erros centralizado, com respostas padronizadas em JSON
- CORS configurado para aceitar requisições do frontend

---

## 🗂️ Estrutura do projeto

```
src/main/java/io/github/jotagevm/daily_planner_api/
├── controller/     # Endpoints REST
├── service/        # Regras de negócio
├── repository/     # Acesso a dados (Spring Data JPA)
├── model/          # Entidades JPA
├── dto/            # Objetos de entrada e saída da API
├── config/         # Configurações (CORS)
└── exception/      # Exceções customizadas e tratamento global de erros
```

---

## ⚙️ Como rodar o projeto localmente

### Pré-requisitos

- JDK 25 (o projeto usa Maven Toolchains — tenha o JDK 25 instalado e declarado em `~/.m2/toolchains.xml`)
- PostgreSQL instalado e rodando
- Maven (ou use o Maven Wrapper incluído no projeto, `./mvnw`)

### 1. Clone o repositório

```bash
git clone https://github.com/JotaGeVM/daily-planner-api.git
cd daily-planner-api
```

### 2. Crie o banco de dados

No PostgreSQL (via pgAdmin ou terminal), crie um banco chamado `daily_planner_db`:

```sql
CREATE DATABASE daily_planner_db;
```

As tabelas são criadas/atualizadas automaticamente pelo Hibernate (`ddl-auto=update`).

### 3. Configure as variáveis de ambiente

O projeto lê as credenciais do banco através de variáveis de ambiente, definidas em `src/main/resources/application.properties`:

```properties
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

Configure `DB_USERNAME` e `DB_PASSWORD` no seu ambiente antes de rodar a aplicação (por exemplo, na configuração de execução da sua IDE, ou exportando as variáveis no terminal).

### 4. Rode a aplicação

```bash
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

---

## 📌 Endpoints da API

### Tarefas

| Método | Rota            | Descrição                     |
| ------ | --------------- | ----------------------------- |
| GET    | `/tarefas`      | Lista todas as tarefas        |
| GET    | `/tarefas/{id}` | Busca uma tarefa pelo ID      |
| POST   | `/tarefas`      | Cadastra uma nova tarefa      |
| PUT    | `/tarefas/{id}` | Atualiza uma tarefa existente |
| DELETE | `/tarefas/{id}` | Remove uma tarefa             |

### Categorias

| Método | Rota               | Descrição                                    |
| ------ | ------------------ | -------------------------------------------- |
| GET    | `/categorias`      | Lista todas as categorias                    |
| GET    | `/categorias/{id}` | Busca uma categoria pelo ID                  |
| POST   | `/categorias`      | Cadastra uma nova categoria                  |
| PUT    | `/categorias/{id}` | Atualiza uma categoria existente             |
| DELETE | `/categorias/{id}` | Remove uma categoria (se não estiver em uso) |

### Ocorrências

| Método | Rota                       | Descrição                          |
| ------ | -------------------------- | ---------------------------------- |
| GET    | `/ocorrencias`             | Lista todas as ocorrências         |
| GET    | `/ocorrencias/tarefa/{id}` | Lista as ocorrências de uma tarefa |
| POST   | `/ocorrencias`             | Registra a conclusão de uma tarefa |
| DELETE | `/ocorrencias/{id}`        | Remove um registro de conclusão    |

---

## 🛡️ Tratamento de erros

A API centraliza o tratamento de exceções através de um `GlobalExceptionHandler`, devolvendo respostas padronizadas nesse formato:

```json
{
  "status": 409,
  "mensagem": "Categoria com nome 'Trabalho' já cadastrada",
  "timestamp": "2026-09-13T18:39:36.545"
}
```

Erros de validação de campo retornam um formato próprio, campo → mensagem:

```json
{
  "nome": "não deve estar em branco"
}
```

Principais cenários tratados:

- **404** — tarefa, categoria ou ocorrência não encontrada
- **409** — categoria com nome duplicado, ou categoria em uso (possui tarefas vinculadas)
- **400** — falha de validação nos dados de entrada, ou corpo da requisição malformado

---

## 🧪 Testes

A API foi testada manualmente via **Insomnia** e **Postman**, cobrindo os fluxos principais (CRUD das três entidades) e os cenários de erro (validação, nome duplicado, recurso não encontrado, categoria em uso, dados órfãos).

---

## ✅ Melhorias implementadas

- Consolidação do modelo de `Ocorrencia`: `data` + `horaConclusao` unificados em um único campo `dataHora`; campo booleano `concluida` removido — a existência do registro passou a representar a conclusão
- Adição de descrição opcional em `Categoria`
- Configuração de CORS para o frontend
- Correção de dependências Maven inválidas no `pom.xml` e configuração de toolchain para build reprodutível em JDK 25
- Tratamento de erros refinado, incluindo respostas estruturadas por campo para erros de validação

---

## 🔭 Próximos passos

- Override de `horaInicio` por ocorrência individual
- Recorrência completa baseada em RRULE
- Autenticação (Spring Security + JWT)
- Testes automatizados com JUnit + Mockito
- Migrations versionadas com Flyway ou Liquibase, em vez de `ddl-auto=update`
- Paginação e ordenação nos endpoints de listagem
- Deploy — colocar a aplicação no ar, com o PostgreSQL também em nuvem

---

## 👤 Autor

Desenvolvido por **João Gustavo** ([@JotaGeVM](https://github.com/JotaGeVM)) como projeto de portfólio.
