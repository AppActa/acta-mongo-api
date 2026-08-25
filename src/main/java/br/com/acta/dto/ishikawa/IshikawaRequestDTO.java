package br.com.acta.dto.ishikawa;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record IshikawaRequestDTO(
        @NotBlank(message = "{validation.ishikawa.problema.notblank}")
        @Schema
        String problema,

        @NotNull(message = "{validation.ishikawa.causas.notnull}")
        @Valid
        @Schema
        List<CausaIshikawaRequestDTO> causas
) {
}
