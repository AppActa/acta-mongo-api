package br.com.acta.dto.licao_aprendida;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record LicaoAprendidaRequestDTO(
        @NotBlank(message = "{validation.licaoAprendida.titulo.notblank}")
        @Schema
        String titulo,

        @NotBlank(message = "{validation.licaoAprendida.licao.notblank}")
        @Schema
        String licao,

        @Schema
        List<String> tags,

        @NotBlank(message = "{validation.licaoAprendida.faseOrigem.notblank}")
        @Schema
        String faseOrigem,

        @NotBlank(message = "{validation.licaoAprendida.resultado.notblank}")
        @Schema
        String resultado,

        @NotBlank(message = "{validation.licaoAprendida.severidade.notblank}")
        @Schema
        String severidade,

        @NotBlank(message = "{validation.licaoAprendida.area.notblank}")
        @Schema
        String area
) {
}
