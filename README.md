# 📅 Daily Planner API

API REST para organização de tarefas, hábitos e categorias, com autenticação multiusuário, recorrência de tarefas, cálculo de streaks e visão de calendário.

Projeto backend de um sistema full stack (Daily Planner), construído para consolidar prática em Spring Boot, persistência com PostgreSQL, autenticação stateless com JWT e testes automatizados.

Repositório do frontend: [daily-planner-web](https://github.com/jotagevm/daily-planner-web)

Aplicação em produção: `https://daily-planner-web-sandy.vercel.app`

## 🛠️ Tecnologias

- Java 25
- Spring Boot 4.1
- Spring Data JPA
- Spring Security
- JWT (JJWT 0.13)
- PostgreSQL (Neon em produção)
- H2 (banco em memória para testes)
- Flyway (versionamento de schema)
- Bean Validation
- JUnit 5 e Mockito
- Maven (com plugin de Toolchains para isolar a JDK do projeto)
- Docker (imagem usada no deploy)

## ✨ Funcionalidades

### 🔐 Autenticação e multiusuário

- Cadastro e login com senha criptografada (BCrypt) e emissão de token JWT.
- Todas as rotas de tarefas, categorias e ocorrências exigem token válido.
- Cada usuário só enxerga e manipula os próprios dados. O isolamento é feito por `usuario_id` em nível de banco e de consulta (`findByIdAndUsuarioId`), de forma que um usuário nunca recebe sequer a confirmação de que um registro de outro usuário existe.

### 📋 Tarefas, categorias e ocorrências

- CRUD completo de tarefas e categorias.
- Categorias com cor associada, usadas para identificação visual no frontend.
- Ocorrências representam o registro de que uma tarefa foi realizada em um momento específico.
- Exclusão de categoria em uso por alguma tarefa é bloqueada.

### 🔥 Hábitos

Tarefas podem ser de três tipos: tarefa única, tarefa recorrente ou hábito.

- Hábitos têm uma meta diária (`metaDiaria`), representando quantas vezes a ação deve ser repetida no dia.
- Cada marcação de progresso gera uma ocorrência; ao atingir a meta do dia, o hábito é considerado cumprido.
- Cálculo de streak (sequência de dias cumpridos consecutivos) e melhor streak histórica, calculados sob demanda a partir das ocorrências existentes, sem campo persistido.

### 🗓️ Recorrência e calendário

- Tarefas recorrentes suportam os padrões diário, semanal (dias da semana específicos), quinzenal (dias da semana em semanas alternadas) e mensal (mesmo dia do mês, com ajuste para meses mais curtos).
- Endpoint de calendário que expande a recorrência de todas as tarefas do usuário em um intervalo de datas, usado pelas visões de semana e mês do frontend.
- Endpoint de "hoje" reaproveita a mesma expansão para montar a lista do dia atual.

### 📄 Paginação e ordenação

- Listagens de tarefas, categorias e ocorrências são paginadas (`Page`), com suporte a ordenação por parâmetro de query.
- Evita carregar coleções inteiras em memória à medida que os dados do usuário crescem.

### ✅ Qualidade e infraestrutura

- Migrações de schema controladas via Flyway (`V1` a `V6`), aplicadas automaticamente na inicialização.
- Suíte de testes unitários com JUnit 5 e Mockito, cobrindo os serviços de tarefas, categorias, ocorrências e o serviço de expansão de recorrência.
- Perfil de teste isolado com H2, sem depender de um banco PostgreSQL local.
- Tratamento de erros centralizado com `GlobalExceptionHandler`, retornando respostas consistentes para validação, recursos não encontrados e conflitos.

## 📂 Estrutura do projeto

src/main/java/io/github/jotagevm/daily_planner_api/
├── config/ Configuração de segurança, CORS e filtro de autenticação JWT
├── controller/ Endpoints REST
├── dto/ Objetos de request e response
├── exception/ Exceções de domínio e handler global
├── model/ Entidades JPA
├── repository/ Interfaces Spring Data JPA
└── service/ Regras de negócio

src/main/resources/
├── db/migration/ Scripts Flyway (V1 a V6)
└── application.properties

src/test/java/... Testes unitários de serviço
src/test/resources/ application.properties do perfil de teste (H2)

## 🚀 Como rodar localmente

Pré-requisitos: JDK 25, Maven, PostgreSQL (ou usar apenas o perfil de teste com H2).

1. Clone o repositório.
2. Configure as variáveis de ambiente:
   - `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`: conexão com o PostgreSQL.
   - `JWT_SECRET`: chave usada para assinar os tokens. Precisa ter pelo menos 256 bits (32 caracteres, recomendado gerar uma string aleatória maior).
3. Rode a aplicação:
   ./mvnw spring-boot:run

As migrações do Flyway são aplicadas automaticamente na primeira execução.

Para rodar os testes:
./mvnw test

O perfil de teste usa H2 em memória e não depende de um banco PostgreSQL disponível.

## 🔌 Endpoints

### Autenticação

| Método | Rota              | Descrição                        |
| ------ | ----------------- | -------------------------------- |
| POST   | `/auth/registrar` | Cria um novo usuário             |
| POST   | `/auth/login`     | Autentica e retorna um token JWT |

### Categorias

| Método | Rota               | Descrição                                |
| ------ | ------------------ | ---------------------------------------- |
| GET    | `/categorias`      | Lista categorias do usuário (paginado)   |
| GET    | `/categorias/{id}` | Busca categoria por id                   |
| POST   | `/categorias`      | Cria categoria                           |
| PUT    | `/categorias/{id}` | Atualiza categoria                       |
| DELETE | `/categorias/{id}` | Remove categoria (se não estiver em uso) |

### Tarefas

| Método | Rota                               | Descrição                                                           |
| ------ | ---------------------------------- | ------------------------------------------------------------------- |
| GET    | `/tarefas`                         | Lista tarefas do usuário (paginado)                                 |
| GET    | `/tarefas/{id}`                    | Busca tarefa por id                                                 |
| POST   | `/tarefas`                         | Cria tarefa (única, recorrente ou hábito)                           |
| PUT    | `/tarefas/{id}`                    | Atualiza tarefa                                                     |
| DELETE | `/tarefas/{id}`                    | Remove tarefa                                                       |
| GET    | `/tarefas/{id}/streak`             | Retorna streak atual e melhor streak de um hábito                   |
| GET    | `/tarefas/calendario?inicio=&fim=` | Expande a recorrência das tarefas do usuário no intervalo informado |

### Ocorrências

| Método | Rota                | Descrição                               |
| ------ | ------------------- | --------------------------------------- |
| GET    | `/ocorrencias`      | Lista ocorrências do usuário (paginado) |
| POST   | `/ocorrencias`      | Registra uma ocorrência para uma tarefa |
| DELETE | `/ocorrencias/{id}` | Remove uma ocorrência                   |

Todas as rotas acima, exceto as de autenticação, exigem o header `Authorization: Bearer <token>`.

## ⚠️ Tratamento de erros

Erros de validação, recursos não encontrados e conflitos de negócio (por exemplo, categoria duplicada ou em uso) são capturados pelo `GlobalExceptionHandler` e retornados em um formato consistente, com status HTTP apropriado e mensagem descritiva.

## ☁️ Deploy

A aplicação roda em container Docker no Render, com o banco PostgreSQL hospedado no Neon. O deploy é automático a cada push na branch principal.

## ✅ Melhorias implementadas ao longo do projeto

- Migração de schema controlada via Flyway.
- Suíte de testes automatizados de serviço.
- Autenticação JWT com isolamento de dados por usuário.
- Paginação e ordenação nas listagens.
- Tipo de tarefa "hábito", com meta diária e cálculo de streak.
- Recorrência de tarefas (diária, semanal, quinzenal, mensal) e endpoint de calendário.

## 🔭 Próximos passos

- Suporte a padrões de recorrência mais flexíveis, no estilo RRULE (intervalos arbitrários, datas de término, exceções).
- Permitir que uma ocorrência individual sobrescreva o horário definido na tarefa recorrente.
- Revisitar a modelagem de `Tarefa` para separar melhor os três tipos (única, recorrente, hábito), hoje representados na mesma entidade.
