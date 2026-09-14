package br.com.acta.document;

import br.com.acta.document.base.AuditoriaBase;
import br.com.acta.document.enums.FormatoRelatorio;
import br.com.acta.document.enums.StatusRelatorio;
import br.com.acta.document.enums.TipoRelatorio;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "relatorios")
@CompoundIndex(
        name = "idx_relatorio_empresa_ciclo_status_criacao",
        def = "{'id_empresa': 1, 'id_ciclo': 1, 'status': 1, 'criado_em': -1}"
)
public class Relatorio extends AuditoriaBase {
    @Field("id_empresa")
    private Long idEmpresa;

    @Field("id_ciclo")
    private Long idCiclo;

    private StatusRelatorio status = StatusRelatorio.RASCUNHO;
    private TipoRelatorio tipo;
    private FormatoRelatorio formato;
    private String titulo;
    private String resumo;
    private String conteudo;

    @Field("id_anexo")
    private Long idAnexo;

    @Field("publicado_em")
    private Instant publicadoEm;
}
