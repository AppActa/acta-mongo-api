package br.com.acta.service;

import br.com.acta.common.client.PgApiClient;
import br.com.acta.common.config.security.UsuarioAutenticado;
import br.com.acta.common.handler.exception.DocumentNotFoundException;
import br.com.acta.common.handler.exception.ImmutableFieldException;
import br.com.acta.common.handler.exception.InexistentFieldException;
import br.com.acta.common.handler.exception.PgApiException;
import br.com.acta.document.Ishikawa;
import br.com.acta.document.embedded.CausaIshikawa;
import br.com.acta.document.enums.CategoriaIshikawa;
import br.com.acta.dto.ishikawa.CausaIshikawaRequestDTO;
import br.com.acta.dto.ishikawa.IshikawaMapper;
import br.com.acta.dto.ishikawa.IshikawaRequestDTO;
import br.com.acta.dto.ishikawa.IshikawaResponseDTO;
import br.com.acta.repository.IshikawaRepository;
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
class IshikawaServiceTest {
    @Mock
    private IshikawaRepository repository;
    @Mock
    private IshikawaMapper mapper;
    @Mock
    private AuthService authService;
    @Mock
    private PgApiClient pgApiClient;

    private IshikawaService service;
    private UsuarioAutenticado usuario;

    @BeforeEach
    void setUp() {
        service = new IshikawaService(repository, mapper, authService, pgApiClient);
        usuario = new UsuarioAutenticado("uid", 7L, 10L, 20L, "Usuário", "u@acta.com",
                "Empresa", "COLABORADOR", false, "ATIVO");
    }

    @Test
    void deveExigirUsuarioAutenticadoNoService() {
        PreAuthorize preAuthorize = IshikawaService.class.getAnnotation(PreAuthorize.class);

        assertNotNull(preAuthorize);
        assertEquals("isAuthenticated()", preAuthorize.value());
    }

    @Test
    void deveBuscarIshikawaSomenteNaEmpresaDoUsuarioAutenticado() {
        UUID id = UUID.randomUUID();
        Ishikawa ishikawa = new Ishikawa();
        when(authService.atual()).thenReturn(usuario);
        when(repository.findByIdAndIdEmpresa(id, 10L)).thenReturn(Optional.of(ishikawa));

        service.buscar(id);

        verify(repository).findByIdAndIdEmpresa(id, 10L);
        verify(repository, never()).findById(id);
    }

    @Test
    void deveOcultarIshikawaDeOutraEmpresaComoNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(authService.atual()).thenReturn(usuario);
        when(repository.findByIdAndIdEmpresa(id, 10L)).thenReturn(Optional.empty());

        assertThrows(DocumentNotFoundException.class, () -> service.buscar(id));
    }

    @Test
    void deveListarSomenteIshikawasDoCicloEDaEmpresaAutenticada() {
        when(authService.atual()).thenReturn(usuario);
        when(pgApiClient.buscarCiclo(25L)).thenReturn(ciclo(10L));
        when(repository.findByIdEmpresaAndIdCiclo(10L, 25L)).thenReturn(List.of());
        when(mapper.toResponseList(List.of())).thenReturn(List.of());

        List<IshikawaResponseDTO> resposta = service.buscarPorCiclo(25L);

        assertEquals(List.of(), resposta);
        verify(repository).findByIdEmpresaAndIdCiclo(10L, 25L);
    }

    @Test
    void deveNegarCicloDeOutraEmpresa() {
        when(authService.atual()).thenReturn(usuario);
        when(pgApiClient.buscarCiclo(25L)).thenReturn(ciclo(99L));

        assertThrows(AccessDeniedException.class, () -> service.buscarPorCiclo(25L));
        verify(repository, never()).findByIdEmpresaAndIdCiclo(10L, 25L);
    }

    @Test
    void deveIdentificarEmpresaECicloAoInserir() {
        IshikawaRequestDTO dto = new IshikawaRequestDTO("Problema", List.of(
                new CausaIshikawaRequestDTO(CategoriaIshikawa.MAQUINA, "Sensor instável")));
        when(authService.atual()).thenReturn(usuario);
        when(pgApiClient.buscarCiclo(25L)).thenReturn(ciclo(10L));
        when(mapper.toEntity(dto)).thenReturn(new Ishikawa());
        when(repository.save(any(Ishikawa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.inserir(25L, dto);

        ArgumentCaptor<Ishikawa> captor = ArgumentCaptor.forClass(Ishikawa.class);
        verify(repository).save(captor.capture());
        assertEquals(10L, captor.getValue().getIdEmpresa());
        assertEquals(25L, captor.getValue().getIdCiclo());
    }

    @Test
    void deveUsarBuscaPorEmpresaAoExcluir() {
        UUID id = UUID.randomUUID();
        Ishikawa ishikawa = new Ishikawa();
        when(authService.atual()).thenReturn(usuario);
        when(repository.findByIdAndIdEmpresa(id, 10L)).thenReturn(Optional.of(ishikawa));

        service.excluir(id);

        verify(repository).findByIdAndIdEmpresa(id, 10L);
        verify(repository).delete(ishikawa);
    }

    @Test
    void deveImpedirAlteracaoDosCamposDeIdentificacao() {
        for (String campo : List.of("id", "idEmpresa", "idCiclo")) {
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
    void deveAlterarSomenteCamposEnviadosNoPatchERetornarMapeamento() {
        UUID id = UUID.randomUUID();
        Ishikawa ishikawa = new Ishikawa();
        ishikawa.setProblema("Problema anterior");
        CausaIshikawa causa = new CausaIshikawa();
        causa.setCategoria(CategoriaIshikawa.METODO);
        causa.setDescricao("Frequência insuficiente");
        IshikawaResponseDTO esperado = mock(IshikawaResponseDTO.class);
        when(authService.atual()).thenReturn(usuario);
        when(repository.findByIdAndIdEmpresa(id, 10L)).thenReturn(Optional.of(ishikawa));
        when(repository.save(ishikawa)).thenReturn(ishikawa);
        when(mapper.toResponse(ishikawa)).thenReturn(esperado);
        IshikawaResponseDTO resposta = service.patch(id,
                Map.of("problema", "Problema novo", "causas", List.of(causa)));

        assertEquals(esperado, resposta);
        assertEquals("Problema novo", ishikawa.getProblema());
        assertEquals(CategoriaIshikawa.METODO, ishikawa.getCausas().get(0).getCategoria());
        assertEquals("Frequência insuficiente", ishikawa.getCausas().get(0).getDescricao());
        verify(repository).save(ishikawa);
    }

    @Test
    void devePropagarFalhaDaPgApiSemConsultarRepositorio() {
        when(authService.atual()).thenReturn(usuario);
        when(pgApiClient.buscarCiclo(25L)).thenThrow(new PgApiException("API indisponível"));

        assertThrows(PgApiException.class, () -> service.buscarPorCiclo(25L));
        verify(repository, never()).findByIdEmpresaAndIdCiclo(any(), any());
    }

    private PgApiClient.CicloPgResponse ciclo(Long idEmpresa) {
        return new PgApiClient.CicloPgResponse(25L, "Ciclo", "ATIVO", idEmpresa, 7L);
    }
}
