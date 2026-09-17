package br.com.acta.service;

import br.com.acta.common.config.security.UsuarioAutenticado;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthServiceTest {
    private final AuthService service = new AuthService();

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveRetornarUsuarioAutenticado() {
        UsuarioAutenticado usuario = new UsuarioAutenticado("uid", 1L, 2L, 3L, "Nome", "email", "Empresa", "ADMIN", false, "ATIVO");
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(usuario, null, usuario.authorities()));
        assertEquals(usuario, service.atual());
    }

    @Test
    void deveRejeitarContextoSemAutenticacaoOuPrincipalInvalido() {
        assertThrows(AuthenticationCredentialsNotFoundException.class, service::atual);
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated("usuario", null, java.util.List.of()));
        assertThrows(AuthenticationCredentialsNotFoundException.class, service::atual);
    }
}
