package br.com.acta.dto.relatorio;

import br.com.acta.document.enums.FormatoRelatorio;
import br.com.acta.document.enums.TipoRelatorio;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RelatorioRequestDTO(
        @NotNull(message = "{validation.relatorio.tipo.notnull}")
        @Schema
        TipoRelatorio tipo,

        @NotNull(message = "{validation.relatorio.formato.notnull}")
        @Schema
        FormatoRelatorio formato,

        @NotBlank(message = "{validation.relatorio.titulo.notblank}")
        @Schema
        String titulo,

        @NotBlank(message = "{validation.relatorio.resumo.notblank}")
        @Schema
        String resumo,

        @NotBlank(message = "{validation.relatorio.conteudo.notblank}")
        @Schema
        String conteudo,

        @Positive(message = "{validation.relatorio.idAnexo.positive}")
        @Schema
        Long idAnexo
) {
}
