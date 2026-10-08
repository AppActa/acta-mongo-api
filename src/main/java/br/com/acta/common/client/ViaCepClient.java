package br.com.acta.common.client;

import br.com.acta.common.handler.exception.InvalidRequestException;
import br.com.acta.common.handler.exception.ViaCepException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.Map;

@Component
public class ViaCepClient {
    private final RestClient restClient;

    public ViaCepClient(
            @Value("${acta.viacep.base-url}") String baseUrl,
            @Value("${acta.viacep.connect-timeout}") Duration connectTimeout,
            @Value("${acta.viacep.read-timeout}") Duration readTimeout
    ) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);
        this.restClient = RestClient.builder().baseUrl(baseUrl).requestFactory(requestFactory).build();
    }

    public Map<String, String> buscarEndereco(String cep) {
        if (cep == null || !cep.matches("[0-9]{8}"))
            throw new InvalidRequestException("O CEP deve conter oito dígitos");

        ViaCepResponse endereco;
        try {
            endereco = restClient.get().uri("{cep}/json/", cep).retrieve().body(ViaCepResponse.class);
        } catch (RestClientException rce) {
            throw new ViaCepException("Não foi possível consultar o ViaCEP");
        }

        if (endereco == null) throw new ViaCepException("O ViaCEP retornou uma resposta vazia");
        if (Boolean.TRUE.equals(endereco.erro())) throw new InvalidRequestException("O CEP informado não foi encontrado");
        if (endereco.uf() == null || !endereco.uf().matches("[A-Z]{2}")
                || endereco.localidade() == null || endereco.localidade().length() > 100
                || endereco.bairro() == null || endereco.bairro().length() > 100
                || endereco.logradouro() == null || endereco.logradouro().length() > 180)
            throw new ViaCepException("O ViaCEP retornou um endereço inválido");

        return Map.of(
                "cep", cep,
                "uf", endereco.uf(),
                "cidade", endereco.localidade(),
                "bairro", endereco.bairro(),
                "logradouro", endereco.logradouro()
        );
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ViaCepResponse(String uf, String localidade, String bairro, String logradouro, Boolean erro) {}
}