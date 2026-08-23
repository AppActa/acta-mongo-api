package br.com.acta.document.embedded;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
public class CausasIshikawa {
    private String metodo;

    @Field("mao_de_obra")
    private String maoObra;

    private String maquina;
    private String material;
    private String medicao;

    @Field("meio_ambiente")
    private String meioAmbiente;
}
