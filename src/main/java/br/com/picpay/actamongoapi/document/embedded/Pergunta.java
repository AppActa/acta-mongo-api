package br.com.picpay.actamongoapi.document.embedded;

import br.com.picpay.actamongoapi.document.enums.TipoResposta;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Pergunta {
    private UUID id;
    private String titulo;
    private TipoResposta tipo;
    private Boolean obrigatoria;
    private List<String> opcoes;
}
