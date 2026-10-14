package br.com.acta.service;

import br.com.acta.common.config.security.UsuarioAutenticado;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class AuthService {
    public UsuarioAutenticado atual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof UsuarioAutenticado usuario) || !auth.isAuthenticated())
            throw new AuthenticationCredentialsNotFoundException("Usuário não autenticado");

        return usuario;
    }

    public String autorizacaoAtual() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes))
            throw new AuthenticationCredentialsNotFoundException("Requisição não disponível");

        String authorization = attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || authorization.isBlank())
            throw new AuthenticationCredentialsNotFoundException("Usuário não autenticado");

        return authorization;
    }
}