package br.com.acta.service;

import br.com.acta.common.handler.exception.DocumentNotFoundException;
import br.com.acta.common.utils.ConversorObject;
import br.com.acta.common.utils.PatchConfig;
import br.com.acta.common.utils.Validador;
import br.com.acta.document.CincoPorques;
import br.com.acta.document.Ishikawa;
import br.com.acta.dto.cinco_porques.CincoPorquesMapper;
import br.com.acta.dto.cinco_porques.CincoPorquesRequestDTO;
import br.com.acta.dto.cinco_porques.CincoPorquesResponseDTO;
import br.com.acta.repository.CincoPorquesRepository;
import br.com.acta.service.base.BaseService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@PreAuthorize("isAuthenticated()")
public class CincoPorquesService extends BaseService<CincoPorquesRequestDTO, CincoPorquesResponseDTO, CincoPorques> {
    private final CincoPorquesRepository repo;
    private final IshikawaService ishikawaService;
    private final PatchConfig patchConfig = new PatchConfig(
            Set.of("idIshikawa", "idCausaIshikawa", "idCausaRaiz", "hipotese", "porques"),
            Set.of("hipotese", "porques")
    );

    public CincoPorquesService(CincoPorquesRepository repo, CincoPorquesMapper mapper, AuthService authService, IshikawaService ishikawaService) {
        super(repo, mapper, authService);
        this.repo = repo;
        this.ishikawaService = ishikawaService;
    }

    @Transactional(readOnly = true)
    public List<CincoPorquesResponseDTO> buscarPorIshikawa(UUID idIshikawa) {
        ishikawaService.getEntity(idIshikawa);
        List<CincoPorques> cincoPorques = repo.findByIdIshikawa(idIshikawa);
        return mapper.toResponseList(cincoPorques);
    }

    @Transactional
    public CincoPorquesResponseDTO inserir(UUID idIshikawa, CincoPorquesRequestDTO dto) {
        Ishikawa ishikawa = ishikawaService.getEntity(idIshikawa);
        validarCausaIshikawa(ishikawa, dto.idCausaIshikawa());

        CincoPorques cincoPorques = mapper.toEntity(dto);
        cincoPorques.setIdIshikawa(idIshikawa);
        cincoPorques.setIdCausaIshikawa(dto.idCausaIshikawa());
        cincoPorques.setIdCausaRaiz(dto.idCausaRaiz());

        CincoPorques salvo = repo.save(cincoPorques);
        return mapper.toResponse(salvo);
    }

    @Override
    protected CincoPorques getEntity(UUID id) {
        CincoPorques cincoPorques = repo.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException("Cinco Porquês", id));
        ishikawaService.getEntity(cincoPorques.getIdIshikawa());
        return cincoPorques;
    }

    @Override
    public CincoPorquesResponseDTO patch(UUID id, Map<String, Object> campos) {
        Validador.validarCampos(campos, patchConfig);
        CincoPorques cincoPorques = getEntity(id);

        if (campos.containsKey("hipotese")) cincoPorques.setHipotese((String) campos.get("hipotese"));
        if (campos.containsKey("porques")) cincoPorques.setPorques(ConversorObject.toPorques(campos.get("porques")));

        CincoPorques salvo = repo.save(cincoPorques);
        return mapper.toResponse(salvo);
    }

    private void validarCausaIshikawa(Ishikawa ishikawa, UUID idCausaIshikawa) {
        boolean causaExiste = ishikawa.getCausas() != null && ishikawa.getCausas().stream()
                .anyMatch(causa -> idCausaIshikawa.equals(causa.getId()));

        if (!causaExiste) throw new DocumentNotFoundException("Causa Ishikawa", idCausaIshikawa);
    }
}
