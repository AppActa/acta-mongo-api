package br.com.acta.service;

import br.com.acta.common.config.security.UsuarioAutenticado;
import br.com.acta.common.handler.exception.DocumentNotFoundException;
import br.com.acta.common.handler.exception.ImmutableFieldException;
import br.com.acta.common.handler.exception.InexistentFieldException;
import br.com.acta.common.validation.RespostaFormularioValidator;
import br.com.acta.document.Formulario;
import br.com.acta.document.RespostaFormulario;
import br.com.acta.document.embedded.RespostaPergunta;
import br.com.acta.dto.resposta_formulario.RespostaFormularioMapper;
import br.com.acta.dto.resposta_formulario.RespostaFormularioRequestDTO;
import br.com.acta.dto.resposta_formulario.RespostaPerguntaMapper;
import br.com.acta.dto.resposta_formulario.RespostaPerguntaRequestDTO;
import br.com.acta.repository.RespostaFormularioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RespostaFormularioServiceTest {
    @Mock
    private RespostaFormularioRepository repository;
    @Mock
    private RespostaFormularioMapper mapper;
    @Mock
    private AuthService authService;
    @Mock
    private FormularioService formularioService;
    @Mock
    private RespostaFormularioValidator validator;
    @Mock
    private RespostaPerguntaMapper respostaPerguntaMapper;

    private RespostaFormularioService service;
    private UsuarioAutenticado usuario;

    @BeforeEach
    void setUp() {
        service = new RespostaFormularioService(
                repository, mapper, authService, formularioService, validator, respostaPerguntaMapper);
        usuario = new UsuarioAutenticado("uid", 7L, 10L, 20L, "Usuário", "u@acta.com",
                "Empresa", "COLABORADOR", false, "ATIVO");
    }

    @Test
    void deveExigirUsuarioAutenticadoNoService() {
        PreAuthorize preAuthorize = RespostaFormularioService.class.getAnnotation(PreAuthorize.class);

        assertNotNull(preAuthorize);
        assertEquals("isAuthenticated()", preAuthorize.value());
    }

    @Test
    void deveListarRespostasDoFormularioNoContextoDaEmpresaEDoCiclo() {
        UUID idFormulario = UUID.randomUUID();
        Formulario formulario = formulario(idFormulario);
        when(formularioService.getEntity(idFormulario)).thenReturn(formulario);
        when(repository.findByIdEmpresaAndIdCicloAndIdFormularioOrderByRespondidoEmDesc(10L, 25L, idFormulario))
                .thenReturn(List.of());
        when(mapper.toResponseList(List.of())).thenReturn(List.of());

        assertEquals(List.of(), service.buscarPorFormulario(idFormulario));

        verify(repository).findByIdEmpresaAndIdCicloAndIdFormularioOrderByRespondidoEmDesc(10L, 25L, idFormulario);
    }

    @Test
    void devePreencherContextoEValidarRespostasAoInserir() {
        UUID idFormulario = UUID.randomUUID();
        Formulario formulario = formulario(idFormulario);
        RespostaFormularioRequestDTO dto = dto(7L);
        when(authService.atual()).thenReturn(usuario);
        when(formularioService.getEntity(idFormulario)).thenReturn(formulario);
        when(mapper.toEntity(dto)).thenReturn(new RespostaFormulario());
        when(repository.save(any(RespostaFormulario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.inserir(idFormulario, dto);

        ArgumentCaptor<RespostaFormulario> captor = ArgumentCaptor.forClass(RespostaFormulario.class);
        verify(repository).save(captor.capture());
        verify(validator).validar(formulario, dto.respostas());
        assertEquals(10L, captor.getValue().getIdEmpresa());
        assertEquals(25L, captor.getValue().getIdCiclo());
        assertEquals(idFormulario, captor.getValue().getIdFormulario());
        assertEquals(7L, captor.getValue().getIdUsuario());
        assertNotNull(captor.getValue().getRespondidoEm());
    }

    @Test
    void deveNegarRespostaEmNomeDeOutroUsuario() {
        UUID idFormulario = UUID.randomUUID();
        when(authService.atual()).thenReturn(usuario);
        when(formularioService.getEntity(idFormulario)).thenReturn(formulario(idFormulario));

        assertThrows(AccessDeniedException.class, () -> service.inserir(idFormulario, dto(99L)));
        verify(validator, never()).validar(any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    void deveBuscarRespostaSomenteNaEmpresaAutenticada() {
        UUID id = UUID.randomUUID();
        RespostaFormulario resposta = new RespostaFormulario();
        when(authService.atual()).thenReturn(usuario);
        when(repository.findByIdAndIdEmpresa(id, 10L)).thenReturn(Optional.of(resposta));

        service.buscar(id);

        verify(repository).findByIdAndIdEmpresa(id, 10L);
        verify(repository, never()).findById(id);
    }

    @Test
    void deveOcultarRespostaDeOutraEmpresaComoNaoEncontrada() {
        UUID id = UUID.randomUUID();
        when(authService.atual()).thenReturn(usuario);
        when(repository.findByIdAndIdEmpresa(id, 10L)).thenReturn(Optional.empty());

        assertThrows(DocumentNotFoundException.class, () -> service.buscar(id));
    }

    @Test
    void deveUsarBuscaPorEmpresaAoExcluir() {
        UUID id = UUID.randomUUID();
        RespostaFormulario resposta = new RespostaFormulario();
        when(authService.atual()).thenReturn(usuario);
        when(repository.findByIdAndIdEmpresa(id, 10L)).thenReturn(Optional.of(resposta));

        service.excluir(id);

        verify(repository).delete(resposta);
    }

    @Test
    void deveImpedirAlteracaoDosCamposDeIdentificacao() {
        for (String campo : List.of("id", "idEmpresa", "idCiclo", "idFormulario", "idUsuario")) {
            assertThrows(ImmutableFieldException.class,
                    () -> service.patch(UUID.randomUUID(), Map.of(campo, 99L)));
        }
        verify(repository, never()).save(any());
    }

    @Test
    void deveRejeitarCampoInexistenteNoPatch() {
        assertThrows(InexistentFieldException.class,
                () -> service.patch(UUID.randomUUID(), Map.of("campoInventado", "valor")));
        verify(repository, never()).save(any());
    }

    @Test
    void deveConverterValidarEAlterarRespostasNoPatch() {
        UUID id = UUID.randomUUID();
        UUID idFormulario = UUID.randomUUID();
        UUID idPergunta = UUID.randomUUID();
        RespostaFormulario resposta = new RespostaFormulario();
        resposta.setIdFormulario(idFormulario);
        Formulario formulario = formulario(idFormulario);
        RespostaPergunta entidade = new RespostaPergunta();
        entidade.setIdPergunta(idPergunta);
        entidade.setResposta("Resposta atualizada");
        List<Map<String, Object>> valor = List.of(Map.of(
                "idPergunta", idPergunta.toString(),
                "resposta", "Resposta atualizada"
        ));
        when(authService.atual()).thenReturn(usuario);
        when(repository.findByIdAndIdEmpresa(id, 10L)).thenReturn(Optional.of(resposta));
        when(formularioService.getEntity(idFormulario)).thenReturn(formulario);
        when(respostaPerguntaMapper.toEntityList(any())).thenReturn(List.of(entidade));
        when(repository.save(resposta)).thenReturn(resposta);

        service.patch(id, Map.of("respostas", valor));

        verify(validator).validar(any(Formulario.class), any());
        assertEquals(List.of(entidade), resposta.getRespostas());
        verify(repository).save(resposta);
    }

    @Test
    void deveImpedirAlteracaoDeRespondidoEmNoPatch() {
        assertThrows(ImmutableFieldException.class,
                () -> service.patch(UUID.randomUUID(), Map.of("respondidoEm", "2026-08-23T18:00:00Z")));
        verify(repository, never()).save(any());
    }

    private Formulario formulario(UUID id) {
        Formulario formulario = new Formulario();
        formulario.setId(id);
        formulario.setIdEmpresa(10L);
        formulario.setIdCiclo(25L);
        return formulario;
    }

    private RespostaFormularioRequestDTO dto(Long idUsuario) {
        return new RespostaFormularioRequestDTO(idUsuario, List.of(
                new RespostaPerguntaRequestDTO(UUID.randomUUID(), "Resposta")));
    }
}
