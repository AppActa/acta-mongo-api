package br.com.picpay.actamongoapi.document.embedded;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RespostaPergunta {
    private UUID idPergunta;
    private Object resposta;
}
