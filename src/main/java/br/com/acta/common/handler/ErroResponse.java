package br.com.acta.common.handler;

import java.time.LocalDateTime;
import java.util.List;

public record ErroResponse(
        List<String> mensagens,
        Integer httpStatus,
        LocalDateTime timestamp
) {
    public ErroResponse(List<String> mensagens, Integer httpStatus) {
        this(mensagens, httpStatus, LocalDateTime.now());
    }
}
