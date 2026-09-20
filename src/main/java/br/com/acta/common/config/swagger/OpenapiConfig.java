package br.com.acta.common.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenapiConfig {
    @Bean
    public OpenAPI actaOpenAPI(){
        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(new SecurityRequirement().addList("firebaseBearer"))
                .components(new Components().addSecuritySchemes("firebaseBearer", firebaseBearer()));
    }

    private Info apiInfo() {
        return new Info()
                .title("ACTA API")
                .description("API MongoDB do ACTA responsável pelo gerenciamento de dados documentais do ciclo PDCA, incluindo diagramas de Ishikawa, análises dos Cinco Porquês, formulários, respostas e lições aprendidas")
                .version("1.0.0")
                .contact(new Contact().name("Equipe ACTA").email("acta.institutojef@gmail.com"))
                .license(new License().name("MIT License").url("https://opensource.org/licenses/MIT"));
    }

    private SecurityScheme firebaseBearer() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("Firebase ID Token")
                .description("ID Token obtido no Firebase Authentication. Não use refresh token nem senha nesta API.");
    }
}
