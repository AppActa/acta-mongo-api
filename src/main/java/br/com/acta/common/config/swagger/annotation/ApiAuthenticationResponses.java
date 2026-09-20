package br.com.acta.common.config.swagger.annotation;

import br.com.acta.common.config.swagger.examples.SwaggerResponseExamples;
import br.com.acta.common.handler.ErroResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "ID Token do Firebase ausente ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(value = SwaggerResponseExamples.ERRO_401))),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar o recurso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(value = SwaggerResponseExamples.ERRO_403)))
})
public @interface ApiAuthenticationResponses {}
