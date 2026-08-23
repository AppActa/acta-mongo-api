package br.com.acta.document.embedded;

import br.com.acta.document.enums.CategoriaIshikawa;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CausaIshikawa {
    private UUID id = UUID.randomUUID();
    private CategoriaIshikawa categoria;
    private String descricao;
}
