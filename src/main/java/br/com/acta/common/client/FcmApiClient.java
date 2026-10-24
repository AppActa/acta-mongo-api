package br.com.acta.common.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class FcmApiClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(FcmApiClient.class);
    private final RestClient.Builder restClientBuilder;
    private final String internalToken;

    public FcmApiClient(
            RestClient.Builder restClientBuilder,
            @Value("${acta.fcm-api.url}") String baseUrl,
            @Value("${acta.fcm-api.internal-token:}") String internalToken,
            @Value("${acta.fcm-api.connect-timeout:3s}") Duration connectTimeout,
            @Value("${acta.fcm-api.read-timeout:5s}") Duration readTimeout
    ) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);

        this.restClientBuilder = restClientBuilder.baseUrl(baseUrl).requestFactory(requestFactory);
        this.internalToken = internalToken;
    }

    public boolean verificarSaude() {
        try {
            return restClientBuilder.build().get().uri("/health").retrieve().toBodilessEntity().getStatusCode().is2xxSuccessful();
        } catch (RestClientException rce) { return false; }
    }

    public void publicarFormularioPublicado(UUID idFormulario, Long idEmpresa, List<Long> destinatarios, Instant instante) {
        if (destinatarios == null || destinatarios.isEmpty()) return;

        String chaveDeduplicacao = "FORMULARIO_PUBLICADO:" + idFormulario + ":" + instante;
        EventoInterno evento = new EventoInterno(
                chaveDeduplicacao, "FORMULARIO_PUBLICADO", instante.toString(), idEmpresa, destinatarios,
                "FORMULARIO", idFormulario.toString(), "acta://formularios/" + idFormulario, chaveDeduplicacao
        );

        try {
            restClientBuilder.build().post().uri("/internal/v1/eventos")
                    .header("X-Internal-Token", internalToken)
                    .body(evento)
                    .retrieve().toBodilessEntity();
        } catch (RestClientException e) {
            LOGGER.warn("Falha ao publicar FORMULARIO_PUBLICADO no acta-fcm-api ({})", e.getClass().getSimpleName());
        }
    }

    public record EventoInterno(
            String idEvento,
            String tipoEvento,
            String ocorridoEm,
            Long idEmpresa,
            List<Long> idsDestinatarios,
            String tipoEntidade,
            String idEntidade,
            String deepLink,
            String chaveDeduplicacao
    ) {}
}