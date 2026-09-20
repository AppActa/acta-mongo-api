package br.com.acta.common.client;

import br.com.acta.common.config.security.UsuarioAutenticado;
import br.com.acta.common.handler.exception.PgApiException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.List;

@Component
public class PgApiClient {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public PgApiClient(
            ObjectMapper objectMapper,
            @Value("${acta.pg-api.base-url}") String baseUrl,
            @Value("${acta.pg-api.connect-timeout}") Duration connectTimeout,
            @Value("${acta.pg-api.read-timeout}") Duration readTimeout
    ) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);

        this.restClient = RestClient.builder().baseUrl(baseUrl).requestFactory(requestFactory).build();
        this.objectMapper = objectMapper;
    }

    public EmpresaPgResponse buscarEmpresa(Long idEmpresa) {
        validarIdentificador(idEmpresa);

        try {
            EmpresaPgResponse empresa = restClient.get()
                    .uri("empresa/{id}", idEmpresa)
                    .retrieve()
                    .body(EmpresaPgResponse.class);

            if (empresa == null) throw new PgApiException();
            return empresa;
        } catch (RestClientResponseException rcre) {
            throw traduzirErro(rcre);
        } catch (ResourceAccessException rae) {
            throw new PgApiException("Não foi possível acessar a API PostgreSQL");
        }
    }

    public CicloPgResponse buscarCiclo(Long idCiclo) {
        validarIdentificador(idCiclo);

        try {
            CicloPgResponse ciclo = restClient.get()
                    .uri("ciclo/{id}", idCiclo)
                    .retrieve()
                    .body(CicloPgResponse.class);

            if (ciclo == null) throw new PgApiException();
            return ciclo;
        } catch (RestClientResponseException rcre) {
            throw traduzirErro(rcre);
        } catch (ResourceAccessException rae) {
            throw new PgApiException("Não foi possível acessar a API PostgreSQL");
        }
    }

    public UsuarioPgResponse buscarUsuario(Long idUsuario) {
        validarIdentificador(idUsuario);

        try {
            UsuarioPgResponse usuario = restClient.get()
                    .uri("usuario/{id}", idUsuario)
                    .retrieve()
                    .body(UsuarioPgResponse.class);

            if (usuario == null) throw new PgApiException();
            return usuario;
        } catch (RestClientResponseException rcre) {
            throw traduzirErro(rcre);
        } catch (ResourceAccessException rae) {
            throw new PgApiException("Não foi possível acessar a API PostgreSQL");
        }
    }

    public UsuarioAutenticado buscarUsuarioAtual(String authorization) {
        try {
            UsuarioAutenticado usuario = restClient.get().uri("me")
                    .header(HttpHeaders.AUTHORIZATION, authorization)
                    .retrieve().body(UsuarioAutenticado.class);

            if (usuario == null) throw new PgApiException();
            return usuario;
        } catch (RestClientResponseException rcre) {
            if (rcre.getStatusCode().value() == 401)
                throw new BadCredentialsException("O ID Token do Firebase não existe ou está inválido");

            if (rcre.getStatusCode().value() == 403)
                throw new AccessDeniedException("Acesso negado");

            throw traduzirErro(rcre);
        } catch (ResourceAccessException rae) {
            throw new PgApiException("Não foi possível acessar a API PostgreSQL");
        }
    }

    private void validarIdentificador(Long id) {
        if (id == null || id <= 0)
            throw new IllegalArgumentException("O identificador deve ser maior que zero");
    }

    private PgApiException traduzirErro(RestClientResponseException rcre) {
        try {
            ErroPgResponse erro = objectMapper.readValue(rcre.getResponseBodyAsString(), ErroPgResponse.class);
            if (erro.mensagens() != null && !erro.mensagens().isEmpty())
                return new PgApiException(String.join("; ", erro.mensagens()));

        } catch (JacksonException je) {}

        String mensagem = "A API PostgreSQL retornou o status " + rcre.getStatusCode().value();
        return new PgApiException(mensagem);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record EmpresaPgResponse(
            Long id,
            String nome,
            String status
    ){}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CicloPgResponse(
            Long id,
            String titulo,
            String status,
            Long idEmpresa,
            Long idGestor
    ){}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record UsuarioPgResponse(
            Long id,
            String nome,
            String email,
            String tipo,
            Long idEmpresa,
            String status
    ){}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ErroPgResponse(List<String> mensagens){}
}
