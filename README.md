# 🧭 ACTA Mongo API

API REST do ACTA para apoiar o ciclo **PDCA** (*Plan, Do, Check, Act*): organiza dados documentais de formulários, respostas, análises de causa, lições aprendidas e registros complementares do aprendizado contínuo.

## 📌 Visão geral

A API é a camada de persistência documental do ecossistema ACTA. Gerencia formulários, respostas de formulários, diagramas de Ishikawa, 5 Porquês, lições aprendidas e modelos de relatório em MongoDB.

Clientes autenticam no Firebase e enviam o **Firebase ID Token** para consumir as rotas protegidas. A validação do usuário autenticado é delegada para a `acta-pg-api`, que concentra identidade, usuários, empresas e ciclos PDCA.

## 🧩 Padrão de projeto: Template Method

A API utiliza uma base comum de operações CRUD na classe [`BaseService`](src/main/java/br/com/acta/service/base/BaseService.java). Ela define o fluxo compartilhado de busca, listagem, inserção, atualização parcial e exclusão dos documentos MongoDB, usando [`BaseRepository`](src/main/java/br/com/acta/repository/base/BaseRepository.java) e [`BaseMapper`](src/main/java/br/com/acta/dto/mapper/base/BaseMapper.java).

O método abstrato protegido `getEntity` funciona como ponto de especialização desse fluxo. Serviços como [`FormularioService`](src/main/java/br/com/acta/service/FormularioService.java), [`IshikawaService`](src/main/java/br/com/acta/service/IshikawaService.java), [`CincoPorquesService`](src/main/java/br/com/acta/service/CincoPorquesService.java), [`RespostaFormularioService`](src/main/java/br/com/acta/service/RespostaFormularioService.java) e [`LicaoAprendidaService`](src/main/java/br/com/acta/service/LicaoAprendidaService.java) implementam essa etapa para localizar o documento correto, tratar ausência de registro e aplicar validações específicas.

Esse padrão foi adotado para **evitar duplicação de código, manter uniforme o processo de persistência documental e permitir que cada serviço especialize somente as etapas necessárias**, sem reimplementar todo o fluxo CRUD.

## 🧠 Padrão de projeto: Strategy

A API também utiliza **Strategy** para validar respostas de formulários. A interface [`ValidacaoRespostaStrategy`](src/main/java/br/com/acta/common/validation/strategy/ValidacaoRespostaStrategy.java) define o contrato de validação, enquanto classes como `ValidacaoTextoStrategy`, `ValidacaoEmailStrategy`, `ValidacaoCpfStrategy`, `ValidacaoCheckboxStrategy`, `ValidacaoRadioStrategy`, `ValidacaoRangeStrategy` e demais estratégias implementam a regra específica de cada [`TipoResposta`](src/main/java/br/com/acta/document/enums/TipoResposta.java).

O componente [`RespostaFormularioValidator`](src/main/java/br/com/acta/common/validation/RespostaFormularioValidator.java) recebe as estratégias pelo Spring, organiza um mapa por tipo de resposta e delega cada validação para a estratégia correspondente. Esse padrão foi adotado para **evitar condicionais extensas, isolar regras de validação por tipo de pergunta e facilitar a inclusão de novos formatos de resposta**.

## ✨ Funcionalidades

- Gestão de formulários por ciclo PDCA.
- Respostas de formulários e validação conforme o tipo de pergunta.
- Diagramas de Ishikawa vinculados a ciclos.
- Registros de 5 Porquês vinculados a Ishikawas.
- Lições aprendidas por ciclo.
- Modelos documentais para relatórios.
- Health check da API e do banco em `GET /api/v1/health`.

## 🛠️ Tecnologias

| Tecnologia | Versão |
| --- | --- |
| Java | 19 |
| Spring Boot | 4.1.1 |
| MongoDB | Driver via Spring Data MongoDB |
| Spring Security | Starter do Spring Boot |
| Springdoc OpenAPI | 3.0.0 |
| MapStruct | 1.6.3 |
| Docker | Dockerfile presente |

Também utiliza Maven Wrapper, Lombok, Bean Validation, Caelum Stella e RestClient.

## ✅ Pré-requisitos e configuração

- JDK 19 e MongoDB acessível.
- `acta-pg-api` acessível para validar o usuário autenticado e consultar dados relacionais como empresa, ciclo e usuário.
- Projeto Firebase configurado no ecossistema ACTA, com clientes enviando Firebase ID Token.
- Docker, caso a execução seja por imagem.

Defina as variáveis abaixo no ambiente ou em um `.env` local. Nunca versione senhas, tokens ou arquivos de credencial.

```env
PORT=8081
MONGODB_URI=mongodb://localhost:27017/acta
ACTA_PG_API_BASE_URL=https://acta-pg-api.onrender.com
ACTA_PG_API_CONNECT_TIMEOUT=3s
ACTA_PG_API_READ_TIMEOUT=5s
```

| Variável | Uso |
| --- | --- |
| `PORT` | Porta HTTP da aplicação |
| `MONGODB_URI` | Conexão MongoDB |
| `ACTA_PG_API_BASE_URL` | URL base da API PostgreSQL usada para autenticação delegada e validações relacionais |
| `ACTA_PG_API_CONNECT_TIMEOUT` | Timeout opcional de conexão com a API PostgreSQL |
| `ACTA_PG_API_READ_TIMEOUT` | Timeout opcional de leitura da API PostgreSQL |

O [`.env.example`](.env.example) é somente uma referência; Spring Boot não carrega `.env` automaticamente sem configuração externa.

## 🚀 Execução local

```powershell
.\mvnw.cmd spring-boot:run
```

```bash
./mvnw spring-boot:run
```

A API usa a porta `8081` por padrão. Com MongoDB configurado, consulte `http://localhost:8081/api/v1/health`.

### Docker

Para criar e executar a imagem:

```powershell
docker build -t acta-mongo-api .
docker run --rm -p 8081:8081 --env-file .env acta-mongo-api
```

Ao executar em contêiner, também disponibilize a URL da `acta-pg-api` e a conexão MongoDB de forma segura; não copie credenciais para a imagem.

## 📚 Documentação e autenticação

- Swagger UI: `http://localhost:8081/swagger-ui/index.html`
- OpenAPI: `http://localhost:8081/v3/api-docs`
- Público: `GET /api/v1/health`, Swagger UI e OpenAPI.
- Demais rotas: `Authorization: Bearer <FIREBASE_ID_TOKEN>`.

Após o login Firebase, envie o Firebase ID Token no cabeçalho `Authorization`. A API repassa esse token para `GET /api/v1/me` da `acta-pg-api` e usa o contexto retornado para autenticar a requisição.

> Custom token, refresh token e senha **não** são Firebase ID Tokens e não devem ser usados no cabeçalho `Authorization`.

## 🔌 Endpoints principais

Todas as rotas abaixo exigem Firebase ID Token, exceto `GET /api/v1/health`.

| Domínio | Rotas |
| --- | --- |
| Formulários | `GET/POST /api/v1/ciclos/{idCiclo}/formularios`, `GET/PATCH/DELETE /api/v1/formularios/{idFormulario}` |
| Respostas de formulário | `GET/POST /api/v1/formularios/{idFormulario}/respostas`, `GET/PATCH/DELETE /api/v1/respostas-formulario/{idRespostaFormulario}` |
| Ishikawa | `GET/POST /api/v1/ciclos/{idCiclo}/ishikawas`, `GET/PATCH/DELETE /api/v1/ishikawas/{idIshikawa}` |
| 5 Porquês | `GET/POST /api/v1/ishikawas/{idIshikawa}/cinco-porques`, `GET/PATCH/DELETE /api/v1/cinco-porques/{idCincoPorques}` |
| Lições aprendidas | `GET/POST /api/v1/ciclos/{idCiclo}/licoes-aprendidas`, `GET/PATCH/DELETE /api/v1/licoes-aprendidas/{idLicaoAprendida}` |

O Swagger contém métodos, parâmetros, enumerações, esquemas e todas as rotas derivadas dos controllers.

## 💻 Exemplos

```bash
curl http://localhost:8081/api/v1/health
```

```bash
curl -X POST http://localhost:8081/api/v1/ciclos/1/formularios \
  -H "Authorization: Bearer <FIREBASE_ID_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Verificação do plano de ação",
    "descricao": "Formulário para acompanhar execução do ciclo.",
    "tipo": "CHECKLIST",
    "perguntas": [],
    "idIshikawa": null,
    "idsUsuariosDestinatarios": [1, 2]
  }'
```

Resposta de saúde, com dados fictícios:

```json
{
  "status": "UP",
  "banco": "UP",
  "mensagem": "O CATO verificou: a API e o banco estão funcionando!",
  "verificadoEm": "2026-09-17T10:30:00-03:00"
}
```

## ⚠️ Erros

O formato confirmado é:

```json
{
  "mensagens": ["campo: mensagem de validação"],
  "httpStatus": 400,
  "timestamp": "2026-09-17T10:35:00"
}
```

| Código | Uso |
| --- | --- |
| 400 | Requisição, parâmetros ou validação inválidos |
| 401 / 403 | Token inválido/ausente ou acesso negado |
| 404 / 405 | Recurso ou método inexistente |
| 502 | Erro retornado pela integração com a API PostgreSQL |
| 500 / 503 | Erro interno ou health check/autenticação delegada indisponível |

## 🏗️ Arquitetura

- `controller/`: rotas HTTP e DTOs.
- `service/`: regras, validações e autenticação delegada; `service/base/BaseService.java` concentra CRUD comum.
- `repository/`: acesso MongoDB.
- `document/`: documentos, embeddeds e enums.
- `dto/` e `dto/mapper/`: contratos e mapeamento MapStruct.
- `common/`: segurança, validação, Strategy de respostas, patch, client PostgreSQL e erros.

A API é stateless. `common/config/security/SecurityConfig.java` registra `ActaPgApiAuthFilter`, enquanto `PgApiClient` consulta a `acta-pg-api` para validar o token recebido e obter o usuário autenticado. `AuthService` e `@PreAuthorize` nos serviços aplicam a exigência de autenticação nas operações protegidas. `RespostaFormularioValidator` aplica Strategy para selecionar a validação correta conforme o tipo de resposta.

## 📁 Estrutura

```text
src/main/java/br/com/acta/
├── common/       # segurança, erros e configurações
├── controller/   # endpoints
├── document/     # documentos e enums
├── dto/          # contratos e mapeadores
├── repository/   # dados
└── service/      # domínio
```

## 🤝 Links e autoria

- [Repositório](https://github.com/AppActa/acta-mongo-api) · [Licença MIT](LICENSE) · `acta.institutojef@gmail.com`
- Contribuições: use *issues* e *pull requests*; há um [`PULL_REQUEST_TEMPLATE.md`](PULL_REQUEST_TEMPLATE.md).
- Autoria: Equipe ACTA.
