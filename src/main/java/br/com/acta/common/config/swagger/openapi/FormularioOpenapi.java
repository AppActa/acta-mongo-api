package br.com.acta.common.config.swagger.openapi;

import br.com.acta.common.config.swagger.examples.SwaggerOpenapiDescriptions;
import br.com.acta.dto.formulario.FormularioRequestDTO;
import br.com.acta.dto.formulario.FormularioResponseDTO;
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

@Tag(name = "Formulários", description = SwaggerOpenapiDescriptions.FORMULARIO_CONTROLLER)
public interface FormularioOpenapi {

    @Operation(summary = "Lista os formulários de um ciclo")
    @ApiResponse(responseCode = "200", description = "Formulários encontrados", content = @Content(array = @ArraySchema(schema = @Schema(implementation = FormularioResponseDTO.class))))
    ResponseEntity<List<FormularioResponseDTO>> buscar(@Parameter(description = "ID do ciclo") Long idCiclo);

    @Operation(summary = "Busca um formulário")
    @ApiResponse(responseCode = "200", description = "Formulário encontrado", content = @Content(schema = @Schema(implementation = FormularioResponseDTO.class)))
    ResponseEntity<FormularioResponseDTO> buscar(@Parameter(description = "ID do formulário") UUID idFormulario);

    @Operation(summary = "Cria um formulário")
    @ApiResponse(responseCode = "201", description = "Formulário criado", content = @Content(schema = @Schema(implementation = FormularioResponseDTO.class)))
    ResponseEntity<FormularioResponseDTO> inserir(@Parameter(description = "ID do ciclo") Long idCiclo, @RequestBody(description = "Dados do formulário", required = true) FormularioRequestDTO dto);

    @Operation(summary = "Atualiza parcialmente um formulário")
    @ApiResponse(responseCode = "200", description = "Formulário atualizado", content = @Content(schema = @Schema(implementation = FormularioResponseDTO.class)))
    ResponseEntity<FormularioResponseDTO> patch(@Parameter(description = "ID do formulário") UUID idFormulario, @RequestBody(description = "Campos do formulário que serão atualizados", required = true) Map<String, Object> campos);

    @Operation(summary = "Exclui um formulário")
    @ApiResponse(responseCode = "204", description = "Formulário excluído")
    ResponseEntity<Void> excluir(@Parameter(description = "ID do formulário") UUID idFormulario);
}
