package br.com.acta.document;

import br.com.acta.document.base.BaseDocument;
import br.com.acta.document.embedded.Porque;
import br.com.acta.document.enums.Classificacao;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "cinco_porques")
@CompoundIndex(
        name = "uk_cinco_porques_id_ishikawa_id_causa_raiz",
        def = "{'id_ishikawa': 1, 'id_causa_raiz': 1}",
        unique = true
)
public class CincoPorques extends BaseDocument {
    @Field("id_ishikawa")
    private UUID idIshikawa;

    @Field("id_causa_raiz")
    private Long idCausaRaiz;
    private String hipotese;
    private Classificacao classificacao;
    private List<Porque> porques;

    @Field("id_criado_por")
    private Long idCriadoPor;

    @Field("criado_em")
    private OffsetDateTime criadoEm;

    @Field("atualizado_em")
    private OffsetDateTime atualizadoEm;
}