package br.com.acta.common.config.swagger.openapi;

import br.com.acta.common.config.swagger.examples.SwaggerOpenapiDescriptions;
import br.com.acta.dto.licao_aprendida.LicaoAprendidaRequestDTO;
import br.com.acta.dto.licao_aprendida.LicaoAprendidaResponseDTO;
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

@Tag(name = "Lições aprendidas", description = SwaggerOpenapiDescriptions.LICAO_APRENDIDA_CONTROLLER)
public interface LicaoAprendidaOpenapi {

    @Operation(summary = "Lista as lições aprendidas de um ciclo")
    @ApiResponse(responseCode = "200", description = "Lições aprendidas encontradas", content = @Content(array = @ArraySchema(schema = @Schema(implementation = LicaoAprendidaResponseDTO.class))))
    ResponseEntity<List<LicaoAprendidaResponseDTO>> buscar(@Parameter(description = "ID do ciclo") Long idCiclo);

    @Operation(summary = "Busca uma lição aprendida")
    @ApiResponse(responseCode = "200", description = "Lição aprendida encontrada", content = @Content(schema = @Schema(implementation = LicaoAprendidaResponseDTO.class)))
    ResponseEntity<LicaoAprendidaResponseDTO> buscar(@Parameter(description = "ID da lição aprendida") UUID idLicaoAprendida);

    @Operation(summary = "Cria uma lição aprendida")
    @ApiResponse(responseCode = "201", description = "Lição aprendida criada", content = @Content(schema = @Schema(implementation = LicaoAprendidaResponseDTO.class)))
    ResponseEntity<LicaoAprendidaResponseDTO> inserir(@Parameter(description = "ID do ciclo") Long idCiclo, @RequestBody(description = "Dados da lição aprendida", required = true) LicaoAprendidaRequestDTO dto);

    @Operation(summary = "Atualiza parcialmente uma lição aprendida")
    @ApiResponse(responseCode = "200", description = "Lição aprendida atualizada", content = @Content(schema = @Schema(implementation = LicaoAprendidaResponseDTO.class)))
    ResponseEntity<LicaoAprendidaResponseDTO> patch(@Parameter(description = "ID da lição aprendida") UUID idLicaoAprendida, @RequestBody(description = "Campos da lição aprendida que serão atualizados", required = true) Map<String, Object> campos);

    @Operation(summary = "Exclui uma lição aprendida")
    @ApiResponse(responseCode = "204", description = "Lição aprendida excluída")
    ResponseEntity<Void> excluir(@Parameter(description = "ID da lição aprendida") UUID idLicaoAprendida);
}
