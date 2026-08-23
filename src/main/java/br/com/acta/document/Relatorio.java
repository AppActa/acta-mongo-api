package br.com.acta.document;

import br.com.acta.document.base.BaseDocument;
import br.com.acta.document.enums.FormatoArquivo;
import br.com.acta.document.enums.StatusRelatorio;
import br.com.acta.document.enums.TipoRelatorio;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "relatorios")
public class Relatorio extends BaseDocument {
    @Field("id_empresa")
    private Long idEmpresa;

    @Field("id_ciclo")
    private Long idCiclo;

    private StatusRelatorio status;
    private TipoRelatorio tipo;
    private FormatoArquivo formato;
    private String titulo;
    private String resumo;
    private String conteudo;

    @Field("criado_por")
    private Long idCriadoPor;

    @Field("criado_em")
    private OffsetDateTime criadoEm;

    @Field("publicado_em")
    private OffsetDateTime publicadoEm;

    @Field("atualizado_em")
    private OffsetDateTime atualizadoEm;
}
