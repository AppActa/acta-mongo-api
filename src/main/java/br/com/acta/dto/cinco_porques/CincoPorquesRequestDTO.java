package br.com.acta.dto.cinco_porques;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public record CincoPorquesRequestDTO(
        @NotNull(message = "{validation.cincoPorques.idCausaIshikawa.notnull}")
        @Schema
        UUID idCausaIshikawa,

        @Positive(message = "{validation.cincoPorques.idCausaRaiz.positive}")
        @Schema
        Long idCausaRaiz,

        @NotBlank(message = "{validation.cincoPorques.hipotese.notblank}")
        @Schema
        String hipotese,

        @NotNull(message = "{validation.cincoPorques.porques.notnull}")
        @Valid
        @Schema List<PorqueRequestDTO> porques
) {
    @JsonIgnore
    @AssertTrue(message = "{validation.cincoPorques.ordem.sequencial}")
    public boolean isSequenciaDosPorquesValida() {
        if (porques.isEmpty() || porques.stream().anyMatch(item -> item == null || item.ordem() == null)) return false;

        long quantidade = porques.size();
        Set<Integer> ordens = porques.stream().map(PorqueRequestDTO::ordem).collect(Collectors.toSet());

        return ordens.size() == quantidade && IntStream.rangeClosed(1, Math.toIntExact(quantidade)).allMatch(ordens::contains);
    }
}
