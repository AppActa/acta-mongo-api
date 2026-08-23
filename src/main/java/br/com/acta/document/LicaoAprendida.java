package br.com.acta.document;

import br.com.acta.document.base.BaseDocument;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "licoes_aprendidas")
public class LicaoAprendida extends BaseDocument {
    @Field("id_empresa")
    private Long idEmpresa;

    @Field("id_ciclo")
    private Long idCiclo;

    private String titulo;
    private String licao;
    private List<String> tags;

    @Field("criado_por")
    private Long idCriadoPor;

    @Field("criado_em")
    private OffsetDateTime criadoEm;
}
