package br.com.acta.dto.ishikawa;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record IshikawaResponseDTO(
        UUID id,
        Long idEmpresa,
        Long idCiclo,
        String problema,
        List<CausaIshikawaResponseDTO> causas,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        Instant criadoEm,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        Instant atualizadoEm
) {
}
