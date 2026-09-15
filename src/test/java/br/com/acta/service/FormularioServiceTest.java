package br.com.acta.service;

import br.com.acta.common.client.PgApiClient;
import br.com.acta.common.config.security.UsuarioAutenticado;
import br.com.acta.common.handler.exception.DocumentNotFoundException;
import br.com.acta.common.handler.exception.ImmutableFieldException;
import br.com.acta.common.handler.exception.InexistentFieldException;
import br.com.acta.common.handler.exception.InvalidRequestException;
import br.com.acta.document.Formulario;
import br.com.acta.document.Ishikawa;
import br.com.acta.document.enums.StatusFormulario;
import br.com.acta.document.enums.TipoFormulario;
import br.com.acta.dto.formulario.FormularioMapper;
import br.com.acta.dto.formulario.FormularioRequestDTO;
import br.com.acta.dto.formulario.FormularioResponseDTO;
import br.com.acta.repository.FormularioRepository;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FormularioServiceTest {
    @Mock
    private FormularioRepository repository;
    @Mock
    private FormularioMapper mapper;
    @Mock
    private AuthService authService;
    @Mock
    private PgApiClient pgApiClient;
    @Mock
    private IshikawaService ishikawaService;

    private FormularioService service;
    private UsuarioAutenticado usuario;

    @BeforeEach
    void setUp() {
        service = new FormularioService(repository, mapper, authService, pgApiClient, ishikawaService);
        usuario = new UsuarioAutenticado("uid", 7L, 10L, 20L, "Usuário", "u@acta.com",
                "Empresa", "COLABORADOR", false, "ATIVO");
    }

    @Test
    void deveExigirUsuarioAutenticadoNoService() {
        PreAuthorize preAuthorize = FormularioService.class.getAnnotation(PreAuthorize.class);

        assertNotNull(preAuthorize);
        assertEquals("isAuthenticated()", preAuthorize.value());
    }

    @Test
    void deveListarSomenteFormulariosDoCicloEDaEmpresaAutenticada() {
        when(authService.atual()).thenReturn(usuario);
        when(pgApiClient.buscarCiclo(25L)).thenReturn(ciclo(10L));
        when(repository.findByIdEmpresaAndIdCiclo(10L, 25L)).thenReturn(List.of());
        when(mapper.toResponseList(List.of())).thenReturn(List.of());

        assertEquals(List.of(), service.buscarPorCiclo(25L));

        verify(repository).findByIdEmpresaAndIdCiclo(10L, 25L);
    }

    @Test
    void deveNegarCicloDeOutraEmpresa() {
        when(authService.atual()).thenReturn(usuario);
        when(pgApiClient.buscarCiclo(25L)).thenReturn(ciclo(99L));

        assertThrows(AccessDeniedException.class, () -> service.buscarPorCiclo(25L));
        verify(repository, never()).findByIdEmpresaAndIdCiclo(any(), any());
    }

    @Test
    void deveIdentificarEmpresaECicloAoInserir() {
        FormularioRequestDTO dto = dto(TipoFormulario.CHECKLIST, null);
        when(authService.atual()).thenReturn(usuario);
        when(pgApiClient.buscarCiclo(25L)).thenReturn(ciclo(10L));
        when(mapper.toEntity(dto)).thenReturn(new Formulario());
        when(repository.save(any(Formulario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.inserir(25L, dto);

        ArgumentCaptor<Formulario> captor = ArgumentCaptor.forClass(Formulario.class);
        verify(repository).save(captor.capture());
        assertEquals(10L, captor.getValue().getIdEmpresa());
        assertEquals(25L, captor.getValue().getIdCiclo());
        verify(ishikawaService, never()).getEntity(any());
    }

    @Test
    void deveExigirIshikawaParaFormularioDesseTipo() {
        FormularioRequestDTO dto = dto(TipoFormulario.ISHIKAWA, null);
        when(authService.atual()).thenReturn(usuario);
        when(pgApiClient.buscarCiclo(25L)).thenReturn(ciclo(10L));

        assertThrows(InvalidRequestException.class, () -> service.inserir(25L, dto));
        verify(repository, never()).save(any());
    }

    @Test
    void deveRejeitarIshikawaDeOutroCiclo() {
        UUID idIshikawa = UUID.randomUUID();
        Ishikawa ishikawa = new Ishikawa();
        ishikawa.setIdCiclo(99L);
        when(authService.atual()).thenReturn(usuario);
        when(pgApiClient.buscarCiclo(25L)).thenReturn(ciclo(10L));
        when(ishikawaService.getEntity(idIshikawa)).thenReturn(ishikawa);

        assertThrows(InvalidRequestException.class,
                () -> service.inserir(25L, dto(TipoFormulario.ISHIKAWA, idIshikawa)));
        verify(repository, never()).save(any());
    }

    @Test
    void deveBuscarFormularioSomenteNaEmpresaAutenticada() {
        UUID id = UUID.randomUUID();
        Formulario formulario = new Formulario();
        when(authService.atual()).thenReturn(usuario);
        when(repository.findByIdAndIdEmpresa(id, 10L)).thenReturn(Optional.of(formulario));

        service.buscar(id);

        verify(repository).findByIdAndIdEmpresa(id, 10L);
        verify(repository, never()).findById(id);
    }

    @Test
    void deveOcultarFormularioDeOutraEmpresaComoNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(authService.atual()).thenReturn(usuario);
        when(repository.findByIdAndIdEmpresa(id, 10L)).thenReturn(Optional.empty());

        assertThrows(DocumentNotFoundException.class, () -> service.buscar(id));
    }

    @Test
    void deveUsarBuscaPorEmpresaAoExcluir() {
        UUID id = UUID.randomUUID();
        Formulario formulario = new Formulario();
        when(authService.atual()).thenReturn(usuario);
        when(repository.findByIdAndIdEmpresa(id, 10L)).thenReturn(Optional.of(formulario));

        service.excluir(id);

        verify(repository).delete(formulario);
    }

    @Test
    void deveImpedirAlteracaoDosCamposDeIdentificacao() {
        for (String campo : List.of("id", "idEmpresa", "idCiclo", "publicadoEm")) {
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
    void deveConverterCamposDoPatchComConversorObject() {
        UUID id = UUID.randomUUID();
        UUID idIshikawa = UUID.randomUUID();
        Formulario formulario = new Formulario();
        formulario.setIdCiclo(25L);
        formulario.setTipo(TipoFormulario.CHECKLIST);
        FormularioResponseDTO esperado = mock(FormularioResponseDTO.class);
        List<Map<String, Object>> perguntasJson = List.of(Map.of(
                "titulo", "Pergunta",
                "tipo", "TEXTO",
                "obrigatoria", true,
                "opcoes", List.of()
        ));
        List<Integer> destinatariosJson = List.of(101, 102);
        Map<String, Object> campos = Map.of(
                "tipo", "ISHIKAWA",
                "status", "ATIVO",
                "perguntas", perguntasJson,
                "idIshikawa", idIshikawa.toString(),
                "idsUsuariosDestinatarios", destinatariosJson
        );
        Ishikawa ishikawa = new Ishikawa();
        ishikawa.setIdCiclo(25L);

        when(authService.atual()).thenReturn(usuario);
        when(repository.findByIdAndIdEmpresa(id, 10L)).thenReturn(Optional.of(formulario));
        when(ishikawaService.getEntity(idIshikawa)).thenReturn(ishikawa);
        when(repository.save(formulario)).thenReturn(formulario);
        when(mapper.toResponse(formulario)).thenReturn(esperado);

        assertEquals(esperado, service.patch(id, campos));

        assertEquals(TipoFormulario.ISHIKAWA, formulario.getTipo());
        assertEquals(StatusFormulario.ATIVO, formulario.getStatus());
        assertEquals("Pergunta", formulario.getPerguntas().get(0).getTitulo());
        assertEquals(idIshikawa, formulario.getIdIshikawa());
        assertEquals(List.of(101L, 102L), formulario.getIdsUsuariosDestinatarios());
        verify(repository).save(formulario);
    }

    private FormularioRequestDTO dto(TipoFormulario tipo, UUID idIshikawa) {
        return new FormularioRequestDTO("Formulário", "Descrição", tipo, List.of(), idIshikawa, List.of(7L));
    }

    private PgApiClient.CicloPgResponse ciclo(Long idEmpresa) {
        return new PgApiClient.CicloPgResponse(25L, "Ciclo", "ATIVO", idEmpresa, 7L);
    }
}
