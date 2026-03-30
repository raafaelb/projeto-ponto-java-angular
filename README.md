# Sistema de Ponto Eletronico

Sistema de gestao de ponto e RH desenvolvido com Angular (frontend) e Spring Boot (backend), com autenticacao JWT, controle por perfis e modulos evolutivos por fase.

## Status atual do projeto

- Fase 1: concluida
- Fase 2: concluida
- Fase 3: concluida
- Fase 4: concluida

Observacao de ambiente:
- O frontend compila com sucesso (`tsc` app/spec).
- No ambiente atual, a execucao de testes/compile do backend via Maven Wrapper pode falhar por problema local no `mvnw.cmd`.

## Tecnologias

- **Frontend**: Angular 15+
- **Backend**: Spring Boot 3.x (Java 17)
- **Banco de dados**: PostgreSQL
- **Autenticacao**: JWT (JSON Web Tokens)
- **Containerizacao**: Docker / Docker Compose

## Funcionalidades implementadas

### 1. Fundacao do sistema (Core)

- [x] Autenticacao com login e token JWT
- [x] Controle de acesso por perfil:
  - `ADMIN`
  - `COMPANY` (gestor da empresa)
  - `EMPLOYEE` (funcionario)
- [x] Cadastro e gerenciamento de empresas
- [x] Cadastro e gerenciamento de usuarios
- [x] Cadastro e gerenciamento de funcionarios
- [x] Cadastro e gerenciamento de departamentos e times
- [x] Organograma (hierarquia por gestor)

### 2. Jornada e ausencias (Fase 2)

- [x] Registro de jornada (clock-in / clock-out)
- [x] Consulta de jornada atual e historico de marcacoes
- [x] Solicitação de ausencia:
  - ferias
  - afastamento medico
  - motivo pessoal
- [x] Workflow de aprovacao/reprovacao de ausencias
- [x] Ajustes/solicitacoes de hora extra com aprovacao
- [x] Cadastro e consulta de feriados
- [x] Deteccao de anomalias de frequencia:
  - atraso
  - falta
  - ausencia de clock-out
- [x] Resolucao de anomalias pelo gestor
- [x] Relatorios consolidados por periodo com exportacao:
  - CSV
  - PDF

### 3. Performance, compensacao e carreira (Fase 3)

#### Performance
- [x] Metas de performance por funcionario
- [x] Acompanhamento de status da meta
- [x] Avaliacoes de desempenho por periodo
- [x] Autoavaliacao do funcionario
- [x] Feedback/fechamento da avaliacao pelo gestor

#### Compensacao
- [x] Ajustes salariais com aprovacao/reprovacao
- [x] Atualizacao de salario atual do funcionario apos aprovacao
- [x] Solicitacoes de bonus por funcionario
- [x] Aprovacao/reprovacao de bonus pelo gestor

#### Carreira e desenvolvimento
- [x] Niveis de carreira por empresa
- [x] Associacao de nivel de carreira ao funcionario
- [x] Registro de skill assessment (nivel atual/alvo)
- [x] Solicitacoes de promocao
- [x] Aprovacao/reprovacao de promocao com atualizacao do nivel

### 4. Folha, beneficios e compliance (Fase 4)

#### Folha de pagamento
- [x] Ciclos de folha (abertura e fechamento)
- [x] Geracao de holerites por funcionario
- [x] Componentes de calculo registrados:
  - bruto
  - descontos
  - impostos retidos
  - hora extra
  - bonus
  - liquido
- [x] Consulta de holerites da empresa e do funcionario

#### Beneficios
- [x] Cadastro de planos de beneficio
- [x] Solicitacao de adesao pelo funcionario
- [x] Aprovacao/cancelamento da adesao pelo gestor
- [x] Consulta de adesoes por empresa e por funcionario

#### Compliance
- [x] Cadastro de politicas corporativas
- [x] Publicacao de politicas ativas para funcionarios
- [x] Reconhecimento (ack) de politica pelo funcionario
- [x] Consulta de reconhecimentos pendentes/concluidos
- [x] Trilha de auditoria de eventos de compliance

## Frontend (menus e portais)

- [x] Portal do gestor de empresa com modulos de:
  - visao geral
  - funcionarios/departamentos/times/organograma
  - ausencias, hora extra, feriados, anomalias e relatorios
  - performance, compensacao, carreira
  - folha, beneficios e compliance
- [x] Portal do funcionario com modulos de:
  - jornada e solicitacoes
  - performance, compensacao, carreira
  - holerites, beneficios e compliance

## Como executar

### Pre-requisitos

- Node.js 16+
- Java 17+
- PostgreSQL 14+
- Docker (opcional)

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

No Windows:

```bash
cd backend
mvnw.cmd spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm start
```

### Via Docker Compose

```bash
docker compose up --build
```

Ou para ambiente de desenvolvimento:

```bash
docker compose -f docker-compose.dev.yml up --build
```

## Testes

### Frontend

```bash
cd frontend
npx tsc -p tsconfig.app.json --noEmit
npx tsc -p tsconfig.spec.json --noEmit
```

### Backend

```bash
cd backend
./mvnw test
```

## Estrutura de perfis e acesso

- `ADMIN`: visao global da plataforma
- `COMPANY`: acesso restrito aos dados da propria empresa
- `EMPLOYEE`: acesso restrito aos proprios dados e fluxos pessoais

## Proximos passos sugeridos

- Fase 5: Analytics, Orcamento e Workforce Planning
  - KPIs executivos
  - simulacao de orcamento de pessoal
  - previsao de headcount e risco de turnover
