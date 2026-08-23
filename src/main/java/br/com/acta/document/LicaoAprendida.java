package br.com.acta.document;

import br.com.acta.document.base.AuditoriaBase;
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
@Document(collection = "licoes_aprendidas")
@CompoundIndex(
        name = "idx_licao_aprendida_empresa_ciclo_criacao",
        def = "{'id_empresa': 1, 'id_ciclo': 1, 'criado_em': -1}"
)
public class LicaoAprendida extends AuditoriaBase {
    @Field("id_empresa")
    private Long idEmpresa;

    @Field("id_ciclo")
    private Long idCiclo;

    private String titulo;
    private String licao;
    private List<String> tags;
    private String faseOrigem;
    private String resultado;
    private String severidade;
    private String area;
}
