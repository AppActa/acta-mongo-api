package br.com.acta.dto.ishikawa;

import br.com.acta.document.enums.CategoriaIshikawa;

import java.util.UUID;

public record CausaIshikawaResponseDTO(
        UUID id,
        CategoriaIshikawa categoria,
        String descricao
) {
}
