package br.com.acta.service;

import br.com.acta.common.client.PgApiClient;
import br.com.acta.common.config.security.UsuarioAutenticado;
import br.com.acta.common.handler.exception.DocumentNotFoundException;
import br.com.acta.common.handler.exception.ImmutableFieldException;
import br.com.acta.common.handler.exception.InexistentFieldException;
import br.com.acta.common.handler.exception.PgApiException;
import br.com.acta.document.LicaoAprendida;
import br.com.acta.dto.licao_aprendida.LicaoAprendidaMapper;
import br.com.acta.dto.licao_aprendida.LicaoAprendidaRequestDTO;
import br.com.acta.dto.licao_aprendida.LicaoAprendidaResponseDTO;
import br.com.acta.repository.LicaoAprendidaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LicaoAprendidaServiceTest {
    @Mock
    private LicaoAprendidaRepository repository;
    @Mock
    private LicaoAprendidaMapper mapper;
    @Mock
    private AuthService authService;
    @Mock
    private PgApiClient pgApiClient;

    private LicaoAprendidaService service;
    private UsuarioAutenticado usuario;

    @BeforeEach
    void setUp() {
        service = new LicaoAprendidaService(repository, mapper, authService, pgApiClient);
        usuario = new UsuarioAutenticado("uid", 7L, 10L, 20L, "Usuário", "u@acta.com",
                "Empresa", "COLABORADOR", false, "ATIVO");
    }

    @Test
    void deveBuscarLicaoSomenteNaEmpresaDoUsuarioAutenticado() {
        UUID id = UUID.randomUUID();
        LicaoAprendida licao = new LicaoAprendida();
        when(authService.atual()).thenReturn(usuario);
        when(repository.findByIdAndIdEmpresa(id, 10L)).thenReturn(Optional.of(licao));

        service.buscar(id);

        verify(repository).findByIdAndIdEmpresa(id, 10L);
        verify(repository, never()).findById(id);
    }

    @Test
    void deveExigirUsuarioAutenticadoNoService() {
        PreAuthorize preAuthorize = LicaoAprendidaService.class.getAnnotation(PreAuthorize.class);

        assertNotNull(preAuthorize);
        assertEquals("isAuthenticated()", preAuthorize.value());

        SecurityContextHolder.clearContext();
        LicaoAprendidaService serviceComAuthReal = new LicaoAprendidaService(
                repository, mapper, new AuthService(), pgApiClient);
        assertThrows(AuthenticationCredentialsNotFoundException.class,
                () -> serviceComAuthReal.buscar(UUID.randomUUID()));
    }

    @Test
    void deveOcultarLicaoDeOutraEmpresaComoNaoEncontrada() {
        UUID id = UUID.randomUUID();
        when(authService.atual()).thenReturn(usuario);
        when(repository.findByIdAndIdEmpresa(id, 10L)).thenReturn(Optional.empty());

        assertThrows(DocumentNotFoundException.class, () -> service.buscar(id));
    }

    @Test
    void deveListarSomenteLicoesDoCicloEDaEmpresaAutenticada() {
        when(authService.atual()).thenReturn(usuario);
        when(pgApiClient.buscarCiclo(25L)).thenReturn(ciclo(10L));
        when(repository.findByIdEmpresaAndIdCicloOrderByCriadoEmDesc(10L, 25L)).thenReturn(List.of());
        when(mapper.toResponseList(List.of())).thenReturn(List.of());

        List<LicaoAprendidaResponseDTO> resposta = service.buscarPorCiclo(25L);

        assertEquals(List.of(), resposta);
        verify(repository).findByIdEmpresaAndIdCicloOrderByCriadoEmDesc(10L, 25L);
    }

    @Test
    void deveNegarCicloDeOutraEmpresa() {
        when(authService.atual()).thenReturn(usuario);
        when(pgApiClient.buscarCiclo(25L)).thenReturn(ciclo(99L));

        assertThrows(AccessDeniedException.class, () -> service.buscarPorCiclo(25L));
        verify(repository, never()).findByIdEmpresaAndIdCicloOrderByCriadoEmDesc(10L, 25L);
    }

    @Test
    void deveIdentificarUsuarioEmpresaECicloAoInserir() {
        LicaoAprendidaRequestDTO dto = new LicaoAprendidaRequestDTO(
                "Título", "Lição", List.of("tag"), "CHECK", "Resultado", "ALTA", "Produção");
        when(authService.atual()).thenReturn(usuario);
        when(pgApiClient.buscarCiclo(25L)).thenReturn(ciclo(10L));
        when(mapper.toEntity(dto)).thenReturn(new LicaoAprendida());
        when(repository.save(any(LicaoAprendida.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.inserir(25L, dto);

        ArgumentCaptor<LicaoAprendida> captor = ArgumentCaptor.forClass(LicaoAprendida.class);
        verify(repository).save(captor.capture());
        assertEquals(7L, captor.getValue().getIdUsuario());
        assertEquals(10L, captor.getValue().getIdEmpresa());
        assertEquals(25L, captor.getValue().getIdCiclo());
    }

    @Test
    void deveUsarBuscaPorEmpresaAoExcluir() {
        UUID id = UUID.randomUUID();
        LicaoAprendida licao = new LicaoAprendida();
        when(authService.atual()).thenReturn(usuario);
        when(repository.findByIdAndIdEmpresa(id, 10L)).thenReturn(Optional.of(licao));

        service.excluir(id);

        verify(repository).findByIdAndIdEmpresa(id, 10L);
        verify(repository).delete(licao);
    }

    @Test
    void deveImpedirAlteracaoDaIdentificacaoDoUsuario() {
        assertThrows(ImmutableFieldException.class,
                () -> service.patch(UUID.randomUUID(), Map.of("idUsuario", 99L)));
        verify(repository, never()).save(any());
    }

    @Test
    void deveImpedirAlteracaoDosDemaisCamposDeIdentificacao() {
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
        LicaoAprendida licao = new LicaoAprendida();
        licao.setTitulo("Título anterior");
        licao.setLicao("Lição preservada");
        LicaoAprendidaResponseDTO esperado = mock(LicaoAprendidaResponseDTO.class);
        when(authService.atual()).thenReturn(usuario);
        when(repository.findByIdAndIdEmpresa(id, 10L)).thenReturn(Optional.of(licao));
        when(repository.save(licao)).thenReturn(licao);
        when(mapper.toResponse(licao)).thenReturn(esperado);

        LicaoAprendidaResponseDTO resposta = service.patch(id,
                Map.of("titulo", "Título novo", "tags", List.of("PDCA", "CHECK")));

        assertEquals(esperado, resposta);
        assertEquals("Título novo", licao.getTitulo());
        assertEquals("Lição preservada", licao.getLicao());
        assertEquals(List.of("PDCA", "CHECK"), licao.getTags());
        verify(repository).save(licao);
    }

    @Test
    void devePropagarFalhaDaPgApiSemConsultarRepositorio() {
        when(authService.atual()).thenReturn(usuario);
        when(pgApiClient.buscarCiclo(25L)).thenThrow(new PgApiException("API indisponível"));

        assertThrows(PgApiException.class, () -> service.buscarPorCiclo(25L));
        verify(repository, never()).findByIdEmpresaAndIdCicloOrderByCriadoEmDesc(any(), any());
    }

    private PgApiClient.CicloPgResponse ciclo(Long idEmpresa) {
        return new PgApiClient.CicloPgResponse(25L, "Ciclo", "ATIVO", idEmpresa, 7L);
    }
}
