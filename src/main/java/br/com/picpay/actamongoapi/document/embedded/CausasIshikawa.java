package br.com.picpay.actamongoapi.document.embedded;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CausasIshikawa {
    private String metodo;
    private String maoObra;
    private String maquina;
    private String material;
    private String medicao;
    private String meioAmbiente;
}
