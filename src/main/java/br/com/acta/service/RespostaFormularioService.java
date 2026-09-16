package br.com.acta.service;

import br.com.acta.common.config.security.UsuarioAutenticado;
import br.com.acta.common.handler.exception.DocumentNotFoundException;
import br.com.acta.common.utils.ConversorObject;
import br.com.acta.common.utils.PatchConfig;
import br.com.acta.common.utils.Validador;
import br.com.acta.common.validation.RespostaFormularioValidator;
import br.com.acta.document.Formulario;
import br.com.acta.document.RespostaFormulario;
import br.com.acta.dto.resposta_formulario.RespostaFormularioMapper;
import br.com.acta.dto.resposta_formulario.RespostaFormularioRequestDTO;
import br.com.acta.dto.resposta_formulario.RespostaFormularioResponseDTO;
import br.com.acta.dto.resposta_formulario.RespostaPerguntaMapper;
import br.com.acta.dto.resposta_formulario.RespostaPerguntaRequestDTO;
import br.com.acta.repository.RespostaFormularioRepository;
import br.com.acta.service.base.BaseService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@PreAuthorize("isAuthenticated()")
public class RespostaFormularioService extends BaseService<RespostaFormularioRequestDTO, RespostaFormularioResponseDTO, RespostaFormulario> {
    private final RespostaFormularioRepository repo;
    private final FormularioService formularioService;
    private final RespostaFormularioValidator validator;
    private final RespostaPerguntaMapper respostaPerguntaMapper;
    private final PatchConfig patchConfig = new PatchConfig(
            Set.of("idEmpresa", "idCiclo", "idFormulario", "idUsuario", "respostas", "respondidoEm"),
            Set.of("respostas")
    );

    public RespostaFormularioService(RespostaFormularioRepository repo, RespostaFormularioMapper mapper, AuthService authService, FormularioService formularioService, RespostaFormularioValidator validator, RespostaPerguntaMapper respostaPerguntaMapper) {
        super(repo, mapper, authService);
        this.repo = repo;
        this.formularioService = formularioService;
        this.validator = validator;
        this.respostaPerguntaMapper = respostaPerguntaMapper;
    }

    @Transactional(readOnly = true)
    public List<RespostaFormularioResponseDTO> buscarPorFormulario(UUID idFormulario) {
        Formulario formulario = formularioService.getEntity(idFormulario);
        List<RespostaFormulario> respostas = repo.findByIdEmpresaAndIdCicloAndIdFormularioOrderByRespondidoEmDesc(formulario.getIdEmpresa(), formulario.getIdCiclo(), idFormulario);
        return mapper.toResponseList(respostas);
    }

    @Transactional
    public RespostaFormularioResponseDTO inserir(UUID idFormulario, RespostaFormularioRequestDTO dto) {
        UsuarioAutenticado usuario = atual();
        Formulario formulario = formularioService.getEntity(idFormulario);

        if (!usuario.idUsuario().equals(dto.idUsuario()))
            throw new AccessDeniedException("O usuário informado não corresponde ao usuário autenticado");

        validator.validar(formulario, dto.respostas());

        RespostaFormulario resposta = mapper.toEntity(dto);
        resposta.setIdEmpresa(usuario.idEmpresa());
        resposta.setIdCiclo(formulario.getIdCiclo());
        resposta.setIdFormulario(idFormulario);
        resposta.setIdUsuario(usuario.idUsuario());
        resposta.setRespondidoEm(Instant.now());

        RespostaFormulario salva = repo.save(resposta);
        return mapper.toResponse(salva);
    }

    @Override
    protected RespostaFormulario getEntity(UUID id) {
        Long idEmpresa = atual().idEmpresa();
        return repo.findByIdAndIdEmpresa(id, idEmpresa).orElseThrow(() -> new DocumentNotFoundException("Resposta de formulário", id));
    }

    @Override
    public RespostaFormularioResponseDTO patch(UUID id, Map<String, Object> campos) {
        Validador.validarCampos(campos, patchConfig);
        RespostaFormulario resposta = getEntity(id);

        if (campos.containsKey("respostas")) {
            Formulario formulario = formularioService.getEntity(resposta.getIdFormulario());
            List<RespostaPerguntaRequestDTO> respostas = ConversorObject.toRespostasPergunta(campos.get("respostas"));
            validator.validar(formulario, respostas);
            resposta.setRespostas(respostaPerguntaMapper.toEntityList(respostas));
        }

        RespostaFormulario salva = repo.save(resposta);
        return mapper.toResponse(salva);
    }
}
