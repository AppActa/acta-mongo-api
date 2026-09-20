package br.com.acta.common.config.swagger.examples;

public final class SwaggerResponseExamples {
    public static final String ERRO_400 = """
            {
              "mensagens": [
                "A requisição informada é inválida"
              ],
              "httpStatus": 400,
              "timestamp": "2026-08-06T12:07:00"
            }
            """;

    public static final String ERRO_401 = """
            {
              "mensagens": [
                "O ID Token do Firebase não existe ou está inválido"
              ],
              "httpStatus": 401,
              "timestamp": "2026-08-06T12:07:00"
            }
            """;

    public static final String ERRO_403 = """
            {
              "mensagens": [
                "Acesso negado"
              ],
              "httpStatus": 403,
              "timestamp": "2026-08-06T12:07:00"
            }
            """;

    public static final String ERRO_404 = """
            {
              "mensagens": [
                "O recurso solicitado não foi encontrado"
              ],
              "httpStatus": 404,
              "timestamp": "2026-08-06T12:07:00"
            }
            """;

    public static final String ERRO_405 = """
            {
              "mensagens": [
                "Método HTTP não permitido para este recurso"
              ],
              "httpStatus": 405,
              "timestamp": "2026-08-06T12:07:00"
            }
            """;

    public static final String ERRO_409 = """
            {
              "mensagens": [
                "Já existe um registro com os dados informados"
              ],
              "httpStatus": 409,
              "timestamp": "2026-08-06T12:07:00"
            }
            """;

    public static final String ERRO_415 = """
            {
              "mensagens": [
                "Tipo de conteúdo não suportado"
              ],
              "httpStatus": 415,
              "timestamp": "2026-08-06T12:07:00"
            }
            """;

    public static final String ERRO_500 = """
            {
              "mensagens": [
                "Ocorreu um erro interno inesperado"
              ],
              "httpStatus": 500,
              "timestamp": "2026-08-06T12:07:00"
            }
            """;

    public static final String ERRO_502 = """
            {
              "mensagens": [
                "A API PostgreSQL retornou uma resposta inválida"
              ],
              "httpStatus": 502,
              "timestamp": "2026-08-06T12:07:00"
            }
            """;

    public static final String ERRO_503 = """
            {
              "mensagens": [
                "Não foi possível acessar a API PostgreSQL"
              ],
              "httpStatus": 503,
              "timestamp": "2026-08-06T12:07:00"
            }
            """;
}