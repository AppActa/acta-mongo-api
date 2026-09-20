package br.com.acta.common.config.swagger.examples;

public final class SwaggerOpenapiDescriptions {
    private SwaggerOpenapiDescriptions() {}

    public static final String CINCO_PORQUES_CONTROLLER = """
            Endpoints responsáveis pelo gerenciamento das análises dos Cinco Porquês vinculadas aos Ishikawas.

            A API permite:

            - Criar e consultar análises dos Cinco Porquês;
            - Atualizar parcialmente uma análise;
            - Excluir uma análise.
            """;

    public static final String FORMULARIO_CONTROLLER = """
            Endpoints responsáveis pelo gerenciamento de formulários do ciclo PDCA.

            A API permite:

            - Criar e consultar formulários por ciclo;
            - Atualizar parcialmente um formulário;
            - Excluir um formulário.
            """;

    public static final String HEALTH_CONTROLLER = """
            Endpoint responsável por verificar a disponibilidade da API e do banco MongoDB.
            """;

    public static final String ISHIKAWA_CONTROLLER = """
            Endpoints responsáveis pelo gerenciamento de diagramas de Ishikawa.

            A API permite:

            - Criar e consultar Ishikawas por ciclo;
            - Atualizar parcialmente um Ishikawa;
            - Excluir um Ishikawa.
            """;

    public static final String LICAO_APRENDIDA_CONTROLLER = """
            Endpoints responsáveis pelo registro e gerenciamento de lições aprendidas do ciclo PDCA.

            A API permite:

            - Criar e consultar lições aprendidas por ciclo;
            - Atualizar parcialmente uma lição aprendida;
            - Excluir uma lição aprendida.
            """;

    public static final String RESPOSTA_FORMULARIO_CONTROLLER = """
            Endpoints responsáveis pelo registro e gerenciamento das respostas dos formulários.

            A API permite:

            - Registrar e consultar respostas de formulários;
            - Atualizar parcialmente uma resposta;
            - Excluir uma resposta.
            """;
}
