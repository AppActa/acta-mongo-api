package br.com.acta.common.config.swagger.openapi;

import br.com.acta.common.config.swagger.examples.SwaggerOpenapiDescriptions;
import br.com.acta.common.config.swagger.annotation.ApiAuthenticationResponses;
import br.com.acta.common.config.swagger.annotation.ApiBadRequestResponse;
import br.com.acta.common.config.swagger.annotation.ApiResourceResponses;
import br.com.acta.common.config.swagger.annotation.ApiUnsupportedMediaTypeResponse;
import br.com.acta.dto.resposta_formulario.RespostaFormularioRequestDTO;
import br.com.acta.dto.resposta_formulario.RespostaFormularioResponseDTO;
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

@Tag(name = "Respostas de formulário", description = SwaggerOpenapiDescriptions.RESPOSTA_FORMULARIO_CONTROLLER)
public interface RespostaFormularioOpenapi {

    @Operation(summary = "Lista as respostas de um formulário")
    @ApiResponse(responseCode = "200", description = "Respostas encontradas", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RespostaFormularioResponseDTO.class))))
    @ApiAuthenticationResponses
    @ApiBadRequestResponse
    ResponseEntity<List<RespostaFormularioResponseDTO>> buscar(@Parameter(description = "ID do formulário") UUID idFormulario);

    @Operation(summary = "Busca uma resposta de formulário")
    @ApiResponse(responseCode = "200", description = "Resposta encontrada", content = @Content(schema = @Schema(implementation = RespostaFormularioResponseDTO.class)))
    @ApiResourceResponses
    ResponseEntity<RespostaFormularioResponseDTO> buscarPorId(@Parameter(description = "ID da resposta de formulário") UUID idRespostaFormulario);

    @Operation(summary = "Registra uma resposta de formulário")
    @ApiResponse(responseCode = "201", description = "Resposta registrada", content = @Content(schema = @Schema(implementation = RespostaFormularioResponseDTO.class)))
    @ApiResourceResponses
    @ApiUnsupportedMediaTypeResponse
    ResponseEntity<RespostaFormularioResponseDTO> inserir(@Parameter(description = "ID do formulário") UUID idFormulario, @RequestBody(description = "Dados da resposta do formulário", required = true) RespostaFormularioRequestDTO dto);

    @Operation(summary = "Atualiza parcialmente uma resposta de formulário")
    @ApiResponse(responseCode = "200", description = "Resposta atualizada", content = @Content(schema = @Schema(implementation = RespostaFormularioResponseDTO.class)))
    @ApiResourceResponses
    @ApiUnsupportedMediaTypeResponse
    ResponseEntity<RespostaFormularioResponseDTO> patch(@Parameter(description = "ID da resposta de formulário") UUID idRespostaFormulario, @RequestBody(description = "Respostas que serão atualizadas", required = true) Map<String, Object> campos);

    @Operation(summary = "Exclui uma resposta de formulário")
    @ApiResponse(responseCode = "204", description = "Resposta excluída")
    @ApiResourceResponses
    ResponseEntity<Void> excluir(@Parameter(description = "ID da resposta de formulário") UUID idRespostaFormulario);
}
