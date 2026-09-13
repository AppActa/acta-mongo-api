package br.com.acta.dto.ishikawa;

import br.com.acta.document.enums.CategoriaIshikawa;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CausaIshikawaRequestDTO(
        @NotNull(message = "{validation.causaIshikawa.categoria.notnull}")
        @Schema
        CategoriaIshikawa categoria,

        @NotBlank(message = "{validation.causaIshikawa.descricao.notblank}")
        @Schema
        String descricao
) {
}
