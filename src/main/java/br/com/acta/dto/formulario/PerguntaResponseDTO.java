package br.com.acta.dto.formulario;

import br.com.acta.document.enums.TipoResposta;

import java.util.List;
import java.util.UUID;

public record PerguntaResponseDTO(
        UUID id,
        String titulo,
        TipoResposta tipo,
        Boolean obrigatoria,
        List<String> opcoes
) {
}
