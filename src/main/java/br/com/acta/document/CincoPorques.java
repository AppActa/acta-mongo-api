package br.com.acta.document;

import br.com.acta.document.base.AuditoriaBase;
import br.com.acta.document.embedded.Porque;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "cinco_porques")
@CompoundIndex(
        name = "uk_cinco_porques_ishikawa_causa",
        def = "{'id_ishikawa': 1, 'id_causa_ishikawa': 1}",
        unique = true
)
public class CincoPorques extends AuditoriaBase {
    @Field("id_ishikawa")
    private UUID idIshikawa;

    @Field("id_causa_ishikawa")
    private UUID idCausaIshikawa;

    @Field("id_causa_raiz")
    private Long idCausaRaiz;

    private String hipotese;
    private List<Porque> porques;
}
