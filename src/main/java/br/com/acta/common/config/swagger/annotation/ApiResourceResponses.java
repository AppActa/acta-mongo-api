package br.com.acta.common.config.swagger.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiAuthenticationResponses
@ApiBadRequestResponse
@ApiNotFoundResponse
public @interface ApiResourceResponses {}