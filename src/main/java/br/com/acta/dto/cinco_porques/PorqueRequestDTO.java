package br.com.acta.dto.cinco_porques;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PorqueRequestDTO(
        @NotNull(message = "{validation.porque.ordem.notnull}")
        @Positive(message = "{validation.porque.ordem.positive}")
        @Schema
        Integer ordem,

        @NotBlank(message = "{validation.porque.pergunta.notblank}")
        @Schema
        String pergunta,

        @NotBlank(message = "{validation.porque.resposta.notblank}")
        @Schema
        String resposta
) {
}
