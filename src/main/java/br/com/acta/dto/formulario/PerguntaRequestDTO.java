package br.com.acta.dto.formulario;

import br.com.acta.document.enums.TipoResposta;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PerguntaRequestDTO(
        @NotBlank(message = "{validation.pergunta.titulo.notblank}")
        @Schema
        String titulo,

        @NotNull(message = "{validation.pergunta.tipo.notnull}")
        @Schema
        TipoResposta tipo,

        @NotNull(message = "{validation.pergunta.obrigatoria.notnull}")
        @Schema
        Boolean obrigatoria,

        @NotNull(message = "{validation.pergunta.opcoes.notnull}")
        @Schema
        List<String> opcoes
) {
}
