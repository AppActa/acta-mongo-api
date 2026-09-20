package br.com.acta.common.config.swagger.openapi;

import br.com.acta.common.config.swagger.examples.SwaggerOpenapiDescriptions;
import br.com.acta.dto.health.HealthResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Saúde", description = SwaggerOpenapiDescriptions.HEALTH_CONTROLLER)
public interface HealthOpenapi {

    @Operation(summary = "Verifica a saúde da API", security = {})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "API e MongoDB disponíveis", content = @Content(schema = @Schema(implementation = HealthResponseDTO.class))),
            @ApiResponse(responseCode = "503", description = "MongoDB indisponível", content = @Content(schema = @Schema(implementation = HealthResponseDTO.class)))
    })
    ResponseEntity<HealthResponseDTO> verificar();
}
