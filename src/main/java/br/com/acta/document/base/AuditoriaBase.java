package br.com.acta.document.base;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public abstract class AuditoriaBase extends BaseDocument {
    @CreatedDate
    @Field("criado_em")
    private Instant criadoEm;

    @LastModifiedDate
    @Field("atualizado_em")
    private Instant atualizadoEm;
}
