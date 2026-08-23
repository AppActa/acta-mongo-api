package br.com.acta.document.embedded;

import br.com.acta.document.enums.TipoResposta;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Pergunta {
    @Field("id_pergunta")
    private UUID id = UUID.randomUUID();

    @Field("texto")
    private String titulo;

    @Field("tipo_resposta")
    private TipoResposta tipo;

    private Boolean obrigatoria;
    private List<String> opcoes;
}
