package br.com.acta.common.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class FcmApiClient {
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

    public void publicarFormularioPublicado(UUID idFormulario, Long idEmpresa, List<Long> destinatarios, Instant instante) {
        if (destinatarios == null || destinatarios.isEmpty()) return;

        EventoInterno evento = new EventoInterno(
                "FORMULARIO_PUBLICADO", instante.toString(), idEmpresa, destinatarios,
                "FORMULARIO", idFormulario.toString(), "acta://formularios/" + idFormulario,
                "FORMULARIO_PUBLICADO:" + idFormulario + ":" + instante
        );

        try {
            restClientBuilder.build().post().uri("/internal/v1/eventos")
                    .header("X-Internal-Token", internalToken)
                    .body(evento)
                    .retrieve().toBodilessEntity();
        } catch (Exception e) {
            throw new IllegalStateException("Não foi possível publicar evento de formulário " + idFormulario + " no acta-fcm-api", e);
        }
    }

    public record EventoInterno(
            String tipo,
            String instante,
            Long empresa,
            List<Long> destinatarios,
            String tipoEntidade,
            String idEntidade,
            String deepLink,
            String chaveDeduplicacao
    ) {}
}