package br.com.acta.common.validation;

import br.com.acta.common.handler.exception.InvalidRequestException;
import br.com.acta.common.validation.strategy.ValidacaoRespostaStrategy;
import br.com.acta.document.Formulario;
import br.com.acta.document.embedded.Pergunta;
import br.com.acta.document.enums.TipoResposta;
import br.com.acta.dto.resposta_formulario.RespostaPerguntaRequestDTO;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class RespostaFormularioValidator {
    private final Map<TipoResposta, ValidacaoRespostaStrategy> strategies;

    public RespostaFormularioValidator(List<ValidacaoRespostaStrategy> strategies) {
        this.strategies = new EnumMap<>(TipoResposta.class);
        strategies.forEach(strategy -> this.strategies.put(strategy.getTipo(), strategy));
    }

    public void validar(Formulario formulario, List<RespostaPerguntaRequestDTO> respostas) {
        List<Pergunta> perguntas = formulario.getPerguntas();
        Map<UUID, Pergunta> perguntasPorId = new HashMap<>();
        perguntas.forEach(pergunta -> perguntasPorId.put(pergunta.getId(), pergunta));

        Set<UUID> idsRespondidos = new HashSet<>();
        for (RespostaPerguntaRequestDTO resposta : respostas) {
            if (!idsRespondidos.add(resposta.idPergunta())) throw new InvalidRequestException("A pergunta " + resposta.idPergunta() + " foi respondida mais de uma vez");

            Pergunta pergunta = perguntasPorId.get(resposta.idPergunta());
            if (pergunta == null) throw new InvalidRequestException("A pergunta " + resposta.idPergunta() + " não pertence ao formulário");
            if (resposta.resposta() == null) throw new InvalidRequestException("A resposta da pergunta '" + pergunta.getTitulo() + "' é obrigatória");

            ValidacaoRespostaStrategy strategy = strategies.get(pergunta.getTipo());
            if (strategy == null) throw new InvalidRequestException("Não existe validação para respostas do tipo " + pergunta.getTipo());

            strategy.validar(pergunta, resposta.resposta());
        }

        perguntas.stream()
                .filter(pergunta -> Boolean.TRUE.equals(pergunta.getObrigatoria()))
                .filter(pergunta -> !idsRespondidos.contains(pergunta.getId()))
                .findFirst()
                .ifPresent(pergunta -> {
                    throw new InvalidRequestException("A pergunta obrigatória '" + pergunta.getTitulo() + "' não foi respondida");
                });
    }
}
