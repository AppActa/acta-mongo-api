package br.com.acta.common.config.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

public record UsuarioAutenticado(
        String firebaseUid,
        Long idUsuario,
        Long idEmpresa,
        Long idColaborador,
        String nome,
        String email,
        String nomeEmpresa,
        String tipo,
        boolean permissaoGestor,
        String status
) implements Principal {
    public List<GrantedAuthority> authorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_" + tipo));

        if (permissaoGestor)
            authorities.add(new SimpleGrantedAuthority("PERMISSAO_GESTOR"));

        return authorities;
    }

    @Override
    public String getName() {
        return firebaseUid;
    }
}
