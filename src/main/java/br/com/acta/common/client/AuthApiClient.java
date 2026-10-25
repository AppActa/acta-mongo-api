package br.com.acta.common.client;

import br.com.acta.common.config.security.UsuarioAutenticado;
import br.com.acta.common.handler.exception.AuthApiException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.List;

@Component
public class AuthApiClient {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public AuthApiClient(
            ObjectMapper objectMapper,
            @Value("${acta.auth-api.base-url}") String baseUrl,
            @Value("${acta.auth-api.connect-timeout}") Duration connectTimeout,
            @Value("${acta.auth-api.read-timeout}") Duration readTimeout
    ) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);
        this.restClient = RestClient.builder().baseUrl(baseUrl).requestFactory(requestFactory).build();
        this.objectMapper = objectMapper;
    }

    public boolean verificarSaude() {
        try {
            return restClient.get().uri("health").retrieve().toBodilessEntity().getStatusCode().is2xxSuccessful();
        } catch (RestClientException rce) { return false; }
    }

    public UsuarioAutenticado buscarUsuarioAtual(String authorization) {
        try {
            UsuarioAutenticado usuario = restClient.get().uri("contexto")
                    .header(HttpHeaders.AUTHORIZATION, authorization)
                    .retrieve().body(UsuarioAutenticado.class);
            if (usuario == null) throw new AuthApiException();
            return usuario;
        } catch (RestClientResponseException rcre) {
            if (rcre.getStatusCode().value() == 401) throw new BadCredentialsException("O ID Token do Firebase não existe ou está inválido");
            if (rcre.getStatusCode().value() == 403) throw new AccessDeniedException("Acesso negado");
            throw traduzirErro(rcre);
        } catch (ResourceAccessException rae) {
            throw new AuthApiException("Não foi possível acessar a API de autenticação");
        }
    }

    private AuthApiException traduzirErro(RestClientResponseException rcre) {
        try {
            ErroAuthResponse erro = objectMapper.readValue(rcre.getResponseBodyAsString(), ErroAuthResponse.class);
            if (erro.mensagens() != null && !erro.mensagens().isEmpty()) return new AuthApiException(String.join("; ", erro.mensagens()));
        } catch (JacksonException je) {}
        return new AuthApiException("A API de autenticação retornou o status " + rcre.getStatusCode().value());
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ErroAuthResponse(List<String> mensagens) {}
}