package br.com.acta.document;

import br.com.acta.document.base.BaseDocument;
import br.com.acta.document.enums.Contexto;
import br.com.acta.document.enums.FormatoArquivo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "anexos")
@CompoundIndex(
        name = "idx_anexo_empresa_ciclo_tipo",
        def = "{'id_empresa': 1, 'id_ciclo': 1, 'tipo_documento': 1}"
)
public class Anexo extends BaseDocument {
    @Field("id_empresa")
    private Long idEmpresa;

    @Field("id_ciclo")
    private Long idCiclo;

    @Field("nome_documento")
    private String nome;

    @Field("tipo_documento")
    private FormatoArquivo tipo;

    private Contexto contexto;
    private Object dados;
}
