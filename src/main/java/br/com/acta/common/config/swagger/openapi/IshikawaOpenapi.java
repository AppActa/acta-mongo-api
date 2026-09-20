package br.com.acta.common.config.swagger.openapi;

import br.com.acta.common.config.swagger.examples.SwaggerOpenapiDescriptions;
import br.com.acta.dto.ishikawa.IshikawaRequestDTO;
import br.com.acta.dto.ishikawa.IshikawaResponseDTO;
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

@Tag(name = "Ishikawas", description = SwaggerOpenapiDescriptions.ISHIKAWA_CONTROLLER)
public interface IshikawaOpenapi {

    @Operation(summary = "Lista os Ishikawas de um ciclo")
    @ApiResponse(responseCode = "200", description = "Ishikawas encontrados", content = @Content(array = @ArraySchema(schema = @Schema(implementation = IshikawaResponseDTO.class))))
    ResponseEntity<List<IshikawaResponseDTO>> buscar(@Parameter(description = "ID do ciclo") Long idCiclo);

    @Operation(summary = "Busca um Ishikawa")
    @ApiResponse(responseCode = "200", description = "Ishikawa encontrado", content = @Content(schema = @Schema(implementation = IshikawaResponseDTO.class)))
    ResponseEntity<IshikawaResponseDTO> buscar(@Parameter(description = "ID do Ishikawa") UUID idIshikawa);

    @Operation(summary = "Cria um Ishikawa")
    @ApiResponse(responseCode = "201", description = "Ishikawa criado", content = @Content(schema = @Schema(implementation = IshikawaResponseDTO.class)))
    ResponseEntity<IshikawaResponseDTO> inserir(@Parameter(description = "ID do ciclo") Long idCiclo, @RequestBody(description = "Dados do Ishikawa", required = true) IshikawaRequestDTO dto);

    @Operation(summary = "Atualiza parcialmente um Ishikawa")
    @ApiResponse(responseCode = "200", description = "Ishikawa atualizado", content = @Content(schema = @Schema(implementation = IshikawaResponseDTO.class)))
    ResponseEntity<IshikawaResponseDTO> patch(@Parameter(description = "ID do Ishikawa") UUID idIshikawa, @RequestBody(description = "Campos do Ishikawa que serão atualizados", required = true) Map<String, Object> campos);

    @Operation(summary = "Exclui um Ishikawa")
    @ApiResponse(responseCode = "204", description = "Ishikawa excluído")
    ResponseEntity<Void> excluir(@Parameter(description = "ID do Ishikawa") UUID idIshikawa);
}
