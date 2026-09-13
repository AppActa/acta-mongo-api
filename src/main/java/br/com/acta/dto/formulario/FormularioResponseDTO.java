package br.com.acta.dto.formulario;

import br.com.acta.document.enums.StatusFormulario;
import br.com.acta.document.enums.TipoFormulario;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record FormularioResponseDTO(
        UUID id,
        Long idEmpresa,
        Long idCiclo,
        String titulo,
        String descricao,
        TipoFormulario tipo,
        StatusFormulario status,
        List<PerguntaResponseDTO> perguntas,
        UUID idIshikawa,
        List<Long> idsUsuariosDestinatarios,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        OffsetDateTime criadoEm,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        OffsetDateTime atualizadoEm,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        OffsetDateTime publicadoEm
) {
}
