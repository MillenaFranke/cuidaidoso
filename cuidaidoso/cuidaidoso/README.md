# Projeto - CuidaIdoso

API REST em Java Spring Boot para o cuidado de idosos em casa. Uma família assina um plano mensal, cadastra os idosos, registra medicamentos com horários, confirma cada dose tomada e organiza rotinas semanais (caminhada, fisioterapia, consultas). A agenda do dia reúne doses e rotinas em ordem de horário.

**Tema ESG (pilar Social):** apoia o envelhecimento digno e a saúde da população idosa, reduzindo esquecimentos de medicação e dando visibilidade do cuidado para toda a família. A aplicação conta com pipeline de CI/CD, containerização com Docker e orquestração com Docker Compose.

**Integrantes**

| Nome | RM |
|------|----|
| Maria Clara Couto Monteiro | 561654 |
| Millena Franke Correa | 566529 |
| Yuri Patrocínio de Azevedo | 562401 |
| Gabriel Sabino Lima | 563168 |
| João Victor Mendes Contarim | 566525 |

## Regras de negócio

- Planos: `BASICO` (até 2 idosos) e `PREMIUM` (sem limite). O limite fica no enum `Plano` e pode ser ajustado em um único lugar.
- A API recusa (409) o cadastro de idoso acima do limite do plano e e-mails de família repetidos.
- Uma dose só pode ser registrada em horário que faça parte da prescrição do medicamento.
- Registrar a mesma dose duas vezes atualiza o registro, sem duplicar.

## Rotas

| Rota | Função |
|------|--------|
| `GET /` | Projeto, ambiente (local, staging ou production) e status |
| `POST /api/familias` | Cadastra família com plano |
| `GET /api/familias/{id}` | Consulta família |
| `POST /api/familias/{id}/idosos` | Cadastra idoso (respeita o limite do plano) |
| `GET /api/familias/{id}/idosos` | Lista os idosos da família |
| `POST /api/idosos/{id}/medicamentos` | Cadastra medicamento com horários |
| `GET /api/idosos/{id}/medicamentos` | Lista medicamentos ativos |
| `PUT /api/medicamentos/{id}/doses` | Registra se a dose do dia/horário foi tomada |
| `POST /api/idosos/{id}/rotinas` | Cadastra rotina semanal |
| `GET /api/idosos/{id}/agenda?data=AAAA-MM-DD` | Agenda do dia (doses e rotinas) |
| `GET /actuator/health` | Saúde da aplicação |

Exemplo de uso:

```bash
curl -X POST http://localhost:8080/api/familias -H "Content-Type: application/json" \
  -d '{"nome":"Família Silva","email":"silva@exemplo.com","plano":"BASICO"}'

curl -X POST http://localhost:8080/api/familias/1/idosos -H "Content-Type: application/json" \
  -d '{"nome":"Dona Maria","dataNascimento":"1945-03-10","observacoes":"Hipertensa"}'

curl -X POST http://localhost:8080/api/idosos/1/medicamentos -H "Content-Type: application/json" \
  -d '{"nome":"Losartana","dosagem":"50mg","horarios":["08:00","20:00"]}'

curl -X PUT http://localhost:8080/api/medicamentos/1/doses -H "Content-Type: application/json" \
  -d '{"data":"2026-10-12","horario":"08:00","tomada":true}'

curl "http://localhost:8080/api/idosos/1/agenda?data=2026-10-12"
```

## Como executar localmente com Docker

Pré-requisitos: Docker e Docker Compose instalados.

1. Clone o repositório e entre na pasta do projeto.
2. Crie o arquivo de variáveis de ambiente a partir do exemplo:
   ```bash
   cp .env.example .env
   ```
3. Ajuste a senha do banco no `.env`.
4. Suba a aplicação e o banco:
   ```bash
   docker compose up --build -d
   ```
5. Acesse a aplicação em `http://localhost:8080` (a rota `/` mostra o ambiente).
6. Para ver os logs e para parar:
   ```bash
   docker compose logs -f app
   docker compose down        # mantém os dados do banco no volume
   docker compose down -v     # apaga também o volume
   ```

Para rodar apenas os testes, sem Docker: `mvn verify` (Java 21 e Maven).

## Pipeline CI/CD

Ferramenta: **GitHub Actions** (arquivo `.github/workflows/ci-cd.yml`).

O pipeline roda a cada push na branch `main`. Em pull requests, roda apenas build e testes.

| Etapa | O que faz |
|-------|-----------|
| Build e testes | Compila com Maven (Java 21) e executa os testes (`PlanoTest` e `CuidaIdosoApiTest`, com H2 em memória) com `mvn verify`. Guarda os relatórios como artefato. |
| Imagem Docker | Constrói a imagem e publica no GitHub Container Registry (GHCR), com tag do commit e `latest`. |
| Deploy em staging | Environment `staging`: copia o compose para o servidor via SSH, sobe a nova imagem e roda um teste de fumaça em `/actuator/health`. |
| Deploy em produção | Environment `production`: roda só depois do staging, exige aprovação manual (Required reviewers) e também termina com teste de fumaça. |

### Configuração necessária no GitHub

Em Settings > Environments, crie `staging` e `production`. No `production`, ative Required reviewers.

Secrets (repositório): `SSH_HOST`, `SSH_USER`, `SSH_KEY`, `GHCR_TOKEN` (token com permissão `read:packages`).

Variables (por environment): `STAGING_URL` e `PRODUCTION_URL` (por exemplo `http://IP-DO-SERVIDOR:8081`).

### Preparação do servidor

Cada ambiente tem uma pasta própria com seu `.env`, usando portas diferentes quando estão no mesmo servidor:

```
~/cuidaidoso/staging/.env      (APP_PORT=8081, APP_AMBIENTE=staging)
~/cuidaidoso/production/.env   (APP_PORT=8080, APP_AMBIENTE=production)
```

O Docker Compose separa containers, rede e volume de cada ambiente pelo nome da pasta. O servidor precisa ter Docker e Docker Compose instalados e a chave pública do `SSH_KEY` autorizada.

## Containerização

O `Dockerfile` usa **multi-stage build**:

```dockerfile
# Estágio 1: build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# Estágio 2: runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S cuida && adduser -S cuida -G cuida
USER cuida
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

Estratégias adotadas:

- **Multi-stage:** a imagem final tem só o JRE e o `.jar`, o que reduz o tamanho.
- **Cache de dependências:** o `pom.xml` é copiado antes do código, então o Maven só baixa tudo de novo quando as dependências mudam.
- **Usuário sem root** para rodar a aplicação.
- **Orquestração:** o `docker-compose.yml` sobe `app` e `db` (PostgreSQL 16), com volume `cuida-pgdata` para persistir dados, rede `cuida-net` e variáveis de ambiente vindas do `.env`. O `app` só inicia depois que o banco passa no healthcheck.

## Prints do funcionamento

(Inserir aqui as evidências)

- Pipeline rodando (build, testes, imagem, deploy):
- Deploy em staging funcionando:
- Deploy em produção funcionando:
- Aplicação rodando localmente com Docker:

## Tecnologias utilizadas

- Java 21 e Spring Boot 3.3 (Web, Data JPA, Validation, Actuator)
- JUnit 5, MockMvc e H2 (testes)
- Maven
- PostgreSQL 16
- Docker e Docker Compose
- GitHub Actions
- GitHub Container Registry (GHCR)

## Checklist de entrega

| Item | OK |
|------|----|
| Projeto compactado em .ZIP com estrutura organizada | ☐ |
| Dockerfile funcional | ☐ |
| docker-compose.yml ou arquivos Kubernetes | ☐ |
| Pipeline com etapas de build, teste e deploy | ☐ |
| README.md com instruções e prints | ☐ |
| Documentação técnica com evidências (PDF ou PPT) | ☐ |
| Deploy realizado nos ambientes staging e produção | ☐ |
