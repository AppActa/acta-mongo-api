package br.com.acta.dto.resposta_formulario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RespostaPerguntaRequestDTO(
        @NotNull(message = "{validation.respostaPergunta.idPergunta.notnull}")
        @Schema
        UUID idPergunta,

        @NotNull(message = "{validation.respostaPergunta.resposta.notnull}")
        @Schema
        Object resposta
) {
}
