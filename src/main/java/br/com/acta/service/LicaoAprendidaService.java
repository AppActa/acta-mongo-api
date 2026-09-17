package br.com.acta.service;

import br.com.acta.common.client.PgApiClient;
import br.com.acta.common.config.security.UsuarioAutenticado;
import br.com.acta.common.handler.exception.DocumentNotFoundException;
import br.com.acta.common.utils.PatchConfig;
import br.com.acta.common.utils.Validador;
import br.com.acta.document.LicaoAprendida;
import br.com.acta.dto.licao_aprendida.LicaoAprendidaMapper;
import br.com.acta.dto.licao_aprendida.LicaoAprendidaRequestDTO;
import br.com.acta.dto.licao_aprendida.LicaoAprendidaResponseDTO;
import br.com.acta.repository.LicaoAprendidaRepository;
import br.com.acta.service.base.BaseService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@PreAuthorize("isAuthenticated()")
public class LicaoAprendidaService extends BaseService<LicaoAprendidaRequestDTO, LicaoAprendidaResponseDTO, LicaoAprendida> {
    private final LicaoAprendidaRepository repo;
    private final PgApiClient pgApiClient;
    private final PatchConfig patchConfig = new PatchConfig(
            Set.of("idUsuario", "idEmpresa", "idCiclo", "titulo", "licao", "tags", "faseOrigem", "resultado", "severidade", "area"),
            Set.of("titulo", "licao", "tags", "faseOrigem", "resultado", "severidade", "area")
    );

    public LicaoAprendidaService(LicaoAprendidaRepository repo, LicaoAprendidaMapper mapper, AuthService authService, PgApiClient pgApiClient) {
        super(repo, mapper, authService);
        this.repo = repo;
        this.pgApiClient = pgApiClient;
    }

    @Transactional(readOnly = true)
    public List<LicaoAprendidaResponseDTO> buscarPorCiclo(Long idCiclo) {
        UsuarioAutenticado usuario = atual();
        validarCicloDaEmpresa(idCiclo, usuario.idEmpresa());

        List<LicaoAprendida> licoes = repo.findByIdEmpresaAndIdCicloOrderByCriadoEmDesc(
                usuario.idEmpresa(), idCiclo);
        return mapper.toResponseList(licoes);
    }

    @Transactional
    public LicaoAprendidaResponseDTO inserir(Long idCiclo, LicaoAprendidaRequestDTO dto) {
        UsuarioAutenticado usuario = atual();
        validarCicloDaEmpresa(idCiclo, usuario.idEmpresa());

        LicaoAprendida licao = mapper.toEntity(dto);
        licao.setIdUsuario(usuario.idUsuario());
        licao.setIdEmpresa(usuario.idEmpresa());
        licao.setIdCiclo(idCiclo);

        LicaoAprendida salvo = repo.save(licao);
        return mapper.toResponse(salvo);
    }

    @Override
    protected LicaoAprendida getEntity(UUID id) {
        Long idEmpresa = atual().idEmpresa();
        return repo.findByIdAndIdEmpresa(id, idEmpresa).orElseThrow(() -> new DocumentNotFoundException("Lição Aprendida", id));
    }

    @Override
    public LicaoAprendidaResponseDTO patch(UUID id, Map<String, Object> campos) {
        Validador.validarCampos(campos, patchConfig);
        LicaoAprendida licao = getEntity(id);

        if (campos.containsKey("titulo")) licao.setTitulo((String) campos.get("titulo"));
        if (campos.containsKey("licao")) licao.setLicao((String) campos.get("licao"));
        if (campos.containsKey("tags")) licao.setTags((List<String>) campos.get("tags"));
        if (campos.containsKey("faseOrigem")) licao.setFaseOrigem((String) campos.get("faseOrigem"));
        if (campos.containsKey("resultado")) licao.setResultado((String) campos.get("resultado"));
        if (campos.containsKey("severidade")) licao.setSeveridade((String) campos.get("severidade"));
        if (campos.containsKey("area")) licao.setArea((String) campos.get("area"));

        LicaoAprendida salvo = repo.save(licao);
        return mapper.toResponse(salvo);
    }

    private void validarCicloDaEmpresa(Long idCiclo, Long idEmpresa) {
        PgApiClient.CicloPgResponse ciclo = pgApiClient.buscarCiclo(idCiclo);
        if (!idEmpresa.equals(ciclo.idEmpresa()))
            throw new AccessDeniedException("O ciclo informado não pertence à empresa do usuário autenticado");
    }

}
