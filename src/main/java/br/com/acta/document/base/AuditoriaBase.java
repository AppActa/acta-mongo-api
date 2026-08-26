package br.com.acta.document.base;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
public abstract class AuditoriaBase extends BaseDocument {
    @Field("criado_em")
    private OffsetDateTime criadoEm;

    @Field("atualizado_em")
    private OffsetDateTime atualizadoEm;
}
