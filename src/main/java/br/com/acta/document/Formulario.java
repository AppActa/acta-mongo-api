package br.com.acta.document;

import br.com.acta.document.base.BaseDocument;
import br.com.acta.document.embedded.Pergunta;
import br.com.acta.document.enums.StatusFormulario;
import br.com.acta.document.enums.TipoFormulario;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "formularios")
@CompoundIndex(
        name = "idx_formulario_empresa_ciclo_status",
        def = "{'id_empresa': 1, 'id_ciclo': 1, 'status': 1}"
)
public class Formulario extends BaseDocument {
    @Field("id_empresa")
    private Long idEmpresa;

    @Field("id_ciclo")
    private Long idCiclo;

    private String titulo;
    private String descricao;
    private TipoFormulario tipo;
    private StatusFormulario status;
    private List<Pergunta> perguntas;

    @Field("criado_por")
    private Long idCriadoPor;

    @Field("criado_em")
    private OffsetDateTime criadoEm;

    @Field("publicado_em")
    private OffsetDateTime publicadoEm;

    @Field("atualizado_em")
    private OffsetDateTime atualizadoEm;
}
