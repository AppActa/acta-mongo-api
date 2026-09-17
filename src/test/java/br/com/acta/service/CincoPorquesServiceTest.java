package br.com.acta.service;

import br.com.acta.common.handler.exception.DocumentNotFoundException;
import br.com.acta.common.handler.exception.ImmutableFieldException;
import br.com.acta.common.handler.exception.InexistentFieldException;
import br.com.acta.document.CincoPorques;
import br.com.acta.document.Ishikawa;
import br.com.acta.document.embedded.CausaIshikawa;
import br.com.acta.document.embedded.Porque;
import br.com.acta.document.enums.CategoriaIshikawa;
import br.com.acta.dto.cinco_porques.CincoPorquesMapper;
import br.com.acta.dto.cinco_porques.CincoPorquesRequestDTO;
import br.com.acta.dto.cinco_porques.CincoPorquesResponseDTO;
import br.com.acta.dto.cinco_porques.PorqueRequestDTO;
import br.com.acta.repository.CincoPorquesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
class CincoPorquesServiceTest {
    @Mock
    private CincoPorquesRepository repository;
    @Mock
    private CincoPorquesMapper mapper;
    @Mock
    private AuthService authService;
    @Mock
    private IshikawaService ishikawaService;

    private CincoPorquesService service;
    private UUID idIshikawa;
    private UUID idCausaIshikawa;
    private Ishikawa ishikawa;

    @BeforeEach
    void setUp() {
        service = new CincoPorquesService(repository, mapper, authService, ishikawaService);
        idIshikawa = UUID.randomUUID();
        idCausaIshikawa = UUID.randomUUID();

        CausaIshikawa causa = new CausaIshikawa();
        causa.setId(idCausaIshikawa);
        causa.setCategoria(CategoriaIshikawa.METODO);
        causa.setDescricao("Frequência insuficiente");

        ishikawa = new Ishikawa();
        ishikawa.setId(idIshikawa);
        ishikawa.setCausas(List.of(causa));
    }

    @Test
    void deveExigirUsuarioAutenticadoNoService() {
        PreAuthorize preAuthorize = CincoPorquesService.class.getAnnotation(PreAuthorize.class);

        assertNotNull(preAuthorize);
        assertEquals("isAuthenticated()", preAuthorize.value());
    }

    @Test
    void deveListarSomenteCincoPorquesDoIshikawaAcessivel() {
        when(ishikawaService.getEntity(idIshikawa)).thenReturn(ishikawa);
        when(repository.findByIdIshikawa(idIshikawa)).thenReturn(List.of());
        when(mapper.toResponseList(List.of())).thenReturn(List.of());

        List<CincoPorquesResponseDTO> resposta = service.buscarPorIshikawa(idIshikawa);

        assertEquals(List.of(), resposta);
        verify(ishikawaService).getEntity(idIshikawa);
        verify(repository).findByIdIshikawa(idIshikawa);
    }

    @Test
    void naoDeveConsultarRepositorioAoListarIshikawaInacessivel() {
        when(ishikawaService.getEntity(idIshikawa)).thenThrow(new DocumentNotFoundException("Ishikawa", idIshikawa));

        assertThrows(DocumentNotFoundException.class, () -> service.buscarPorIshikawa(idIshikawa));
        verify(repository, never()).findByIdIshikawa(any());
    }

    @Test
    void deveBuscarCincoPorquesSomenteQuandoSeuIshikawaForAcessivel() {
        UUID id = UUID.randomUUID();
        CincoPorques cincoPorques = new CincoPorques();
        cincoPorques.setIdIshikawa(idIshikawa);
        when(repository.findById(id)).thenReturn(Optional.of(cincoPorques));
        when(ishikawaService.getEntity(idIshikawa)).thenReturn(ishikawa);

        service.buscar(id);

        verify(ishikawaService).getEntity(idIshikawa);
    }

    @Test
    void deveOcultarCincoPorquesDeIshikawaInacessivelComoNaoEncontrado() {
        UUID id = UUID.randomUUID();
        CincoPorques cincoPorques = new CincoPorques();
        cincoPorques.setIdIshikawa(idIshikawa);
        when(repository.findById(id)).thenReturn(Optional.of(cincoPorques));
        when(ishikawaService.getEntity(idIshikawa))
                .thenThrow(new DocumentNotFoundException("Ishikawa", idIshikawa));

        assertThrows(DocumentNotFoundException.class, () -> service.buscar(id));
    }

    @Test
    void deveVincularIshikawaECausaAoInserir() {
        CincoPorquesRequestDTO dto = dto(idCausaIshikawa);
        when(ishikawaService.getEntity(idIshikawa)).thenReturn(ishikawa);
        when(mapper.toEntity(dto)).thenReturn(new CincoPorques());
        when(repository.save(any(CincoPorques.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.inserir(idIshikawa, dto);

        ArgumentCaptor<CincoPorques> captor = ArgumentCaptor.forClass(CincoPorques.class);
        verify(repository).save(captor.capture());
        assertEquals(idIshikawa, captor.getValue().getIdIshikawa());
        assertEquals(idCausaIshikawa, captor.getValue().getIdCausaIshikawa());
        assertEquals(50L, captor.getValue().getIdCausaRaiz());
    }

    @Test
    void deveRejeitarCausaQueNaoPertenceAoIshikawa() {
        UUID outraCausa = UUID.randomUUID();
        when(ishikawaService.getEntity(idIshikawa)).thenReturn(ishikawa);

        assertThrows(DocumentNotFoundException.class, () -> service.inserir(idIshikawa, dto(outraCausa)));
        verify(repository, never()).save(any());
    }

    @Test
    void deveUsarValidacaoDoIshikawaAoExcluir() {
        UUID id = UUID.randomUUID();
        CincoPorques cincoPorques = new CincoPorques();
        cincoPorques.setIdIshikawa(idIshikawa);
        when(repository.findById(id)).thenReturn(Optional.of(cincoPorques));
        when(ishikawaService.getEntity(idIshikawa)).thenReturn(ishikawa);

        service.excluir(id);

        verify(ishikawaService).getEntity(idIshikawa);
        verify(repository).delete(cincoPorques);
    }

    @Test
    void deveImpedirAlteracaoDosCamposDeIdentificacao() {
        for (String campo : List.of("id", "idIshikawa", "idCausaIshikawa", "idCausaRaiz")) {
            assertThrows(ImmutableFieldException.class,
                    () -> service.patch(UUID.randomUUID(), Map.of(campo, UUID.randomUUID())));
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
        CincoPorques cincoPorques = new CincoPorques();
        cincoPorques.setIdIshikawa(idIshikawa);
        cincoPorques.setHipotese("Hipótese anterior");
        Porque porque = new Porque();
        porque.setOrdem(1);
        porque.setPergunta("Por quê?");
        porque.setResposta("Porque sim");
        CincoPorquesResponseDTO esperado = mock(CincoPorquesResponseDTO.class);
        when(repository.findById(id)).thenReturn(Optional.of(cincoPorques));
        when(ishikawaService.getEntity(idIshikawa)).thenReturn(ishikawa);
        when(repository.save(cincoPorques)).thenReturn(cincoPorques);
        when(mapper.toResponse(cincoPorques)).thenReturn(esperado);
        CincoPorquesResponseDTO resposta = service.patch(id,
                Map.of("hipotese", "Hipótese nova", "porques", List.of(porque)));

        assertEquals(esperado, resposta);
        assertEquals("Hipótese nova", cincoPorques.getHipotese());
        assertEquals(1, cincoPorques.getPorques().get(0).getOrdem());
        assertEquals("Por quê?", cincoPorques.getPorques().get(0).getPergunta());
        assertEquals("Porque sim", cincoPorques.getPorques().get(0).getResposta());
        verify(repository).save(cincoPorques);
    }

    private CincoPorquesRequestDTO dto(UUID idCausa) {
        return new CincoPorquesRequestDTO(idCausa, 50L, "Hipótese", List.of(
                new PorqueRequestDTO(1, "Por quê?", "Porque sim")));
    }
}
