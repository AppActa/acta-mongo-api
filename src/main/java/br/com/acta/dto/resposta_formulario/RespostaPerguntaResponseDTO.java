package br.com.acta.dto.resposta_formulario;

import java.util.UUID;

public record RespostaPerguntaResponseDTO(
        UUID idPergunta,
        Object resposta
) {
}
