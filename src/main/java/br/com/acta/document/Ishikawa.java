package br.com.acta.document;

import br.com.acta.document.base.AuditoriaBase;
import br.com.acta.document.embedded.CausaIshikawa;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "ishikawas")
@CompoundIndex(
        name = "idx_ishikawa_empresa_ciclo",
        def = "{'id_empresa': 1, 'id_ciclo': 1}"
)
public class Ishikawa extends AuditoriaBase {
    @Field("id_empresa")
    private Long idEmpresa;

    @Field("id_ciclo")
    private Long idCiclo;

    private String problema;

    private List<CausaIshikawa> causas;
}
