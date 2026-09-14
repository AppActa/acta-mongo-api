package br.com.acta.service;

import br.com.acta.common.client.PgApiClient;
import br.com.acta.common.config.security.UsuarioAutenticado;
import br.com.acta.common.handler.exception.DocumentNotFoundException;
import br.com.acta.common.handler.exception.InvalidRequestException;
import br.com.acta.common.utils.ConversorObject;
import br.com.acta.common.utils.PatchConfig;
import br.com.acta.common.utils.Validador;
import br.com.acta.document.Formulario;
import br.com.acta.document.Ishikawa;
import br.com.acta.document.enums.TipoFormulario;
import br.com.acta.dto.formulario.FormularioMapper;
import br.com.acta.dto.formulario.FormularioRequestDTO;
import br.com.acta.dto.formulario.FormularioResponseDTO;
import br.com.acta.repository.FormularioRepository;
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
public class FormularioService extends BaseService<FormularioRequestDTO, FormularioResponseDTO, Formulario> {
    private final FormularioRepository repo;
    private final PgApiClient pgApiClient;
    private final IshikawaService ishikawaService;
    private final PatchConfig patchConfig = new PatchConfig(
            Set.of("idEmpresa", "idCiclo", "titulo", "descricao", "tipo", "status", "perguntas", "idIshikawa", "idsUsuariosDestinatarios", "publicadoEm"),
            Set.of("titulo", "descricao", "tipo", "status", "perguntas", "idIshikawa", "idsUsuariosDestinatarios")
    );

    public FormularioService(FormularioRepository repo, FormularioMapper mapper, AuthService authService, PgApiClient pgApiClient, IshikawaService ishikawaService) {
        super(repo, mapper, authService);
        this.repo = repo;
        this.pgApiClient = pgApiClient;
        this.ishikawaService = ishikawaService;
    }

    @Transactional(readOnly = true)
    public List<FormularioResponseDTO> buscarPorCiclo(Long idCiclo) {
        UsuarioAutenticado usuario = atual();
        validarCicloDaEmpresa(idCiclo, usuario.idEmpresa());

        List<Formulario> formularios = repo.findByIdEmpresaAndIdCiclo(usuario.idEmpresa(), idCiclo);
        return mapper.toResponseList(formularios);
    }

    @Transactional
    public FormularioResponseDTO inserir(Long idCiclo, FormularioRequestDTO dto) {
        UsuarioAutenticado usuario = atual();
        validarCicloDaEmpresa(idCiclo, usuario.idEmpresa());
        validarVinculoIshikawa(dto.tipo(), dto.idIshikawa(), idCiclo);

        Formulario formulario = mapper.toEntity(dto);
        formulario.setIdEmpresa(usuario.idEmpresa());
        formulario.setIdCiclo(idCiclo);

        Formulario salvo = repo.save(formulario);
        return mapper.toResponse(salvo);
    }

    @Override
    protected Formulario getEntity(UUID id) {
        Long idEmpresa = atual().idEmpresa();
        return repo.findByIdAndIdEmpresa(id, idEmpresa).orElseThrow(() -> new DocumentNotFoundException("Formulário", id));
    }

    @Override
    public FormularioResponseDTO patch(UUID id, Map<String, Object> campos) {
        Validador.validarCampos(campos, patchConfig);
        Formulario formulario = getEntity(id);

        if (campos.containsKey("titulo")) formulario.setTitulo((String) campos.get("titulo"));
        if (campos.containsKey("descricao")) formulario.setDescricao((String) campos.get("descricao"));
        if (campos.containsKey("tipo")) formulario.setTipo(ConversorObject.toTipoFormulario(campos.get("tipo")));
        if (campos.containsKey("status")) formulario.setStatus(ConversorObject.toStatusFormulario(campos.get("status")));
        if (campos.containsKey("perguntas")) formulario.setPerguntas(ConversorObject.toPerguntas(campos.get("perguntas")));
        if (campos.containsKey("idIshikawa")) formulario.setIdIshikawa(ConversorObject.toUUID(campos.get("idIshikawa"), "idIshikawa"));
        if (campos.containsKey("idsUsuariosDestinatarios")) formulario.setIdsUsuariosDestinatarios(ConversorObject.toLongList(campos.get("idsUsuariosDestinatarios"), "idsUsuariosDestinatarios"));

        validarVinculoIshikawa(formulario.getTipo(), formulario.getIdIshikawa(), formulario.getIdCiclo());

        Formulario salvo = repo.save(formulario);
        return mapper.toResponse(salvo);
    }

    private void validarCicloDaEmpresa(Long idCiclo, Long idEmpresa) {
        PgApiClient.CicloPgResponse ciclo = pgApiClient.buscarCiclo(idCiclo);
        if (!idEmpresa.equals(ciclo.idEmpresa()))
            throw new AccessDeniedException("O ciclo informado não pertence à empresa do usuário autenticado");
    }

    private void validarVinculoIshikawa(TipoFormulario tipo, UUID idIshikawa, Long idCiclo) {
        if (tipo == TipoFormulario.ISHIKAWA && idIshikawa == null)
            throw new InvalidRequestException("O campo idIshikawa é obrigatório para formulários do tipo ISHIKAWA");

        Ishikawa ishikawa = ishikawaService.getEntity(idIshikawa);
        if (!idCiclo.equals(ishikawa.getIdCiclo()))
            throw new InvalidRequestException("O Ishikawa informado não pertence ao ciclo do formulário");
    }
}