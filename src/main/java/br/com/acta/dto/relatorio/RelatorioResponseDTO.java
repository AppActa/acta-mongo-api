package br.com.acta.dto.relatorio;

import br.com.acta.document.enums.FormatoRelatorio;
import br.com.acta.document.enums.StatusRelatorio;
import br.com.acta.document.enums.TipoRelatorio;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.UUID;

public record RelatorioResponseDTO(
        UUID id,
        Long idEmpresa,
        Long idCiclo,
        StatusRelatorio status,
        TipoRelatorio tipo,
        FormatoRelatorio formato,
        String titulo,
        String resumo,
        String conteudo,
        Long idAnexo,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        Instant criadoEm,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        Instant atualizadoEm,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        Instant publicadoEm
) {
}
