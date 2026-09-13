package br.com.acta.dto.formulario;

import br.com.acta.document.enums.TipoFormulario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.UUID;

public record FormularioRequestDTO(
        @NotBlank(message = "{validation.formulario.titulo.notblank}")
        @Schema
        String titulo,

        @Schema
        String descricao,

        @NotNull(message = "{validation.formulario.tipo.notnull}")
        @Schema
        TipoFormulario tipo,

        @NotNull(message = "{validation.formulario.perguntas.notnull}")
        @Valid
        @Schema
        List<PerguntaRequestDTO> perguntas,

        @Schema
        UUID idIshikawa,

        @NotNull(message = "{validation.formulario.idsUsuariosDestinatarios.notnull}")
        @Schema
        List<@Positive(message = "{validation.formulario.idsUsuariosDestinatarios.positive}") Long> idsUsuariosDestinatarios
) {
}
