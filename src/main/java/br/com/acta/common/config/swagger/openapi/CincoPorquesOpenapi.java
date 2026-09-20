package br.com.acta.common.config.swagger.openapi;

import br.com.acta.common.config.swagger.examples.SwaggerOpenapiDescriptions;
import br.com.acta.dto.cinco_porques.CincoPorquesRequestDTO;
import br.com.acta.dto.cinco_porques.CincoPorquesResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Cinco Porquês", description = SwaggerOpenapiDescriptions.CINCO_PORQUES_CONTROLLER)
public interface CincoPorquesOpenapi {

    @Operation(summary = "Lista as análises dos Cinco Porquês de um Ishikawa")
    @ApiResponse(responseCode = "200", description = "Análises encontradas", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CincoPorquesResponseDTO.class))))
    ResponseEntity<List<CincoPorquesResponseDTO>> buscar(@Parameter(description = "ID do Ishikawa") UUID idIshikawa);

    @Operation(summary = "Busca uma análise dos Cinco Porquês")
    @ApiResponse(responseCode = "200", description = "Análise encontrada", content = @Content(schema = @Schema(implementation = CincoPorquesResponseDTO.class)))
    ResponseEntity<CincoPorquesResponseDTO> buscarPorId(@Parameter(description = "ID da análise dos Cinco Porquês") UUID idCincoPorques);

    @Operation(summary = "Cria uma análise dos Cinco Porquês")
    @ApiResponse(responseCode = "201", description = "Análise criada", content = @Content(schema = @Schema(implementation = CincoPorquesResponseDTO.class)))
    ResponseEntity<CincoPorquesResponseDTO> inserir(@Parameter(description = "ID do Ishikawa") UUID idIshikawa, @RequestBody(description = "Dados da análise dos Cinco Porquês", required = true) CincoPorquesRequestDTO dto);

    @Operation(summary = "Atualiza parcialmente uma análise dos Cinco Porquês")
    @ApiResponse(responseCode = "200", description = "Análise atualizada", content = @Content(schema = @Schema(implementation = CincoPorquesResponseDTO.class)))
    ResponseEntity<CincoPorquesResponseDTO> patch(@Parameter(description = "ID da análise dos Cinco Porquês") UUID idCincoPorques, @RequestBody(description = "Campos da análise que serão atualizados", required = true) Map<String, Object> campos);

    @Operation(summary = "Exclui uma análise dos Cinco Porquês")
    @ApiResponse(responseCode = "204", description = "Análise excluída")
    ResponseEntity<Void> excluir(@Parameter(description = "ID da análise dos Cinco Porquês") UUID idCincoPorques);
}
