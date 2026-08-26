package br.com.acta.dto.resposta_formulario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record RespostaFormularioRequestDTO(
        @NotNull(message = "{validation.respostaFormulario.idUsuario.notnull}")
        @Positive(message = "{validation.respostaFormulario.idUsuario.positive}")
        @Schema
        Long idUsuario,

        @NotNull(message = "{validation.respostaFormulario.respostas.notnull}")
        @Valid
        @Schema
        List<RespostaPerguntaRequestDTO> respostas
) {
}
