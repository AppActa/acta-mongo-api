package br.com.acta.dto.resposta_formulario;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record RespostaFormularioResponseDTO(
        UUID id,
        Long idEmpresa,
        Long idCiclo,
        UUID idFormulario,
        Long idUsuario,
        List<RespostaPerguntaResponseDTO> respostas,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        OffsetDateTime respondidoEm
) {
}
