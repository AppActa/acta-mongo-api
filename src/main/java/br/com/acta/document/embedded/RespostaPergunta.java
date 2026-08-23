package br.com.acta.document.embedded;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RespostaPergunta {
    @Field("id_pergunta")
    private UUID idPergunta;

    private Object resposta;
}
