package br.com.acta.common.config.security;

import br.com.acta.common.client.PgApiClient;
import br.com.acta.common.handler.ErroResponse;
import br.com.acta.common.handler.exception.PgApiException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RequiredArgsConstructor
public class ActaPgApiAuthFilter extends OncePerRequestFilter {
    private final PgApiClient client;
    private final ObjectMapper mapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return "/api/v1/health".equals(request.getServletPath());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || authorization.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            UsuarioAutenticado usuario = client.buscarUsuarioAtual(authorization);
            autenticar(request, usuario);
            filterChain.doFilter(request, response);
        } catch (BadCredentialsException bce) {
            SecurityContextHolder.clearContext();
            responderErro(response, HttpStatus.UNAUTHORIZED, "O ID Token do Firebase não existe ou está inválido");
        } catch (AccessDeniedException ade) {
            SecurityContextHolder.clearContext();
            responderErro(response, HttpStatus.FORBIDDEN, "Acesso negado");
        } catch (PgApiException pae) {
            SecurityContextHolder.clearContext();
            responderErro(response, HttpStatus.SERVICE_UNAVAILABLE, pae.getMessage());
        }
    }

    private void autenticar(HttpServletRequest request, UsuarioAutenticado usuario) {
        UsernamePasswordAuthenticationToken auth = UsernamePasswordAuthenticationToken.authenticated(usuario, null, usuario.authorities());
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }

    private void responderErro(HttpServletResponse response, HttpStatus status, String mensagem) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        mapper.writeValue(response.getOutputStream(), new ErroResponse(List.of(mensagem), status.value()));
    }
}
