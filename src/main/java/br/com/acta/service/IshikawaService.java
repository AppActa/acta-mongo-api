package br.com.acta.service;

import br.com.acta.common.client.PgApiClient;
import br.com.acta.common.config.security.UsuarioAutenticado;
import br.com.acta.common.handler.exception.DocumentNotFoundException;
import br.com.acta.common.utils.PatchConfig;
import br.com.acta.common.utils.Validador;
import br.com.acta.common.utils.ConversorObject;
import br.com.acta.document.Ishikawa;
import br.com.acta.dto.ishikawa.IshikawaMapper;
import br.com.acta.dto.ishikawa.IshikawaRequestDTO;
import br.com.acta.dto.ishikawa.IshikawaResponseDTO;
import br.com.acta.repository.IshikawaRepository;
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
public class IshikawaService extends BaseService<IshikawaRequestDTO, IshikawaResponseDTO, Ishikawa> {
    private final IshikawaRepository repo;
    private final PgApiClient pgApiClient;
    private final PatchConfig patchConfig = new PatchConfig(
            Set.of("idEmpresa", "idCiclo", "problema", "causas"),
            Set.of("problema", "causas")
    );

    public IshikawaService(IshikawaRepository repo, IshikawaMapper mapper, AuthService authService, PgApiClient pgApiClient) {
        super(repo, mapper, authService);
        this.repo = repo;
        this.pgApiClient = pgApiClient;
    }

    @Transactional(readOnly = true)
    public List<IshikawaResponseDTO> buscarPorCiclo(Long idCiclo) {
        UsuarioAutenticado usuario = atual();
        validarCicloDaEmpresa(idCiclo, usuario.idEmpresa());

        List<Ishikawa> ishikawas = repo.findByIdEmpresaAndIdCiclo(usuario.idEmpresa(), idCiclo);
        return mapper.toResponseList(ishikawas);
    }

    @Transactional
    public IshikawaResponseDTO inserir(Long idCiclo, IshikawaRequestDTO dto) {
        UsuarioAutenticado usuario = atual();
        validarCicloDaEmpresa(idCiclo, usuario.idEmpresa());

        Ishikawa ishikawa = mapper.toEntity(dto);
        ishikawa.setIdEmpresa(usuario.idEmpresa());
        ishikawa.setIdCiclo(idCiclo);

        Ishikawa salvo = repo.save(ishikawa);
        return mapper.toResponse(salvo);
    }

    @Override
    protected Ishikawa getEntity(UUID id) {
        Long idEmpresa = atual().idEmpresa();
        return repo.findByIdAndIdEmpresa(id, idEmpresa)
                .orElseThrow(() -> new DocumentNotFoundException("Ishikawa", id));
    }

    @Override
    public IshikawaResponseDTO patch(UUID id, Map<String, Object> campos) {
        Validador.validarCampos(campos, patchConfig);
        Ishikawa ishikawa = getEntity(id);

        if (campos.containsKey("problema")) ishikawa.setProblema((String) campos.get("problema"));
        if (campos.containsKey("causas")) ishikawa.setCausas(ConversorObject.toCausasIshikawa(campos.get("causas")));

        Ishikawa salvo = repo.save(ishikawa);
        return mapper.toResponse(salvo);
    }

    private void validarCicloDaEmpresa(Long idCiclo, Long idEmpresa) {
        PgApiClient.CicloPgResponse ciclo = pgApiClient.buscarCiclo(idCiclo);
        if (!idEmpresa.equals(ciclo.idEmpresa())) {
            throw new AccessDeniedException("O ciclo informado não pertence à empresa do usuário autenticado");
        }
    }
}
