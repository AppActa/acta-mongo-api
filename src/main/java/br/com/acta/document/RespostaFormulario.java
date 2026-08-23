package br.com.acta.document;

import br.com.acta.document.base.BaseDocument;
import br.com.acta.document.embedded.RespostaPergunta;
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
@Document(collection = "respostas_formulario")
@CompoundIndex(
        name = "idx_resposta_formulario_empresa_ciclo_formulario_data",
        def = "{'id_empresa': 1, 'id_ciclo': 1, 'id_formulario': 1, 'respondido_em': -1}"
)
public class RespostaFormulario extends BaseDocument {
    @Field("id_empresa")
    private Long idEmpresa;

    @Field("id_ciclo")
    private Long idCiclo;

    @Field("id_formulario")
    private UUID idFormulario;

    @Field("id_usuario")
    private Long idUsuario;

    @Field("respostas")
    private List<RespostaPergunta> respostas;

    @Field("respondido_em")
    private OffsetDateTime respondidoEm;
}
