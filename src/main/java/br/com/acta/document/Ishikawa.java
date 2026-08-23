package br.com.acta.document;

import br.com.acta.document.base.BaseDocument;
import br.com.acta.document.embedded.CausasIshikawa;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "ishikawa")
@CompoundIndex(
        name = "uk_ishikawa_empresa_ciclo",
        def = "{'id_empresa': 1, 'id_ciclo': 1}",
        unique = true
)
public class Ishikawa extends BaseDocument {
    @Field("id_empresa")
    private Long idEmpresa;

    @Field("id_ciclo")
    private Long idCiclo;

    private String problema;
    private CausasIshikawa causas;

    @Field("criado_por")
    private Long idCriadoPor;

    @Field("criado_em")
    private OffsetDateTime criadoEm;

    @Field("atualizado_em")
    private OffsetDateTime atualizadoEm;
}
