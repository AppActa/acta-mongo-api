package br.com.acta.controller;

import br.com.acta.dto.cinco_porques.*;
import br.com.acta.dto.formulario.*;
import br.com.acta.dto.health.*;
import br.com.acta.dto.ishikawa.*;
import br.com.acta.dto.licao_aprendida.*;
import br.com.acta.dto.resposta_formulario.*;
import br.com.acta.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ControllerTest {
    @Test
    void deveDelegarOperacoesDoCincoPorquesComStatusCorreto() {
        CincoPorquesService service = mock(CincoPorquesService.class);
        CincoPorquesController controller = new CincoPorquesController(service);
        UUID pai = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        CincoPorquesRequestDTO request = mock(CincoPorquesRequestDTO.class);
        CincoPorquesResponseDTO response = mock(CincoPorquesResponseDTO.class);
        when(service.buscarPorIshikawa(pai)).thenReturn(List.of(response));
        when(service.buscar(id)).thenReturn(response);
        when(service.inserir(pai, request)).thenReturn(response);
        when(service.patch(id, Map.of())).thenReturn(response);

        assertEquals(HttpStatus.OK, controller.buscar(pai).getStatusCode());
        assertEquals(response, controller.buscarPorId(id).getBody());
        assertEquals(HttpStatus.CREATED, controller.inserir(pai, request).getStatusCode());
        assertEquals(response, controller.patch(id, Map.of()).getBody());
        assertEquals(HttpStatus.NO_CONTENT, controller.excluir(id).getStatusCode());
        verify(service).excluir(id);
    }

    @Test
    void deveDelegarOperacoesDoIshikawaComStatusCorreto() {
        IshikawaService service = mock(IshikawaService.class);
        IshikawaController controller = new IshikawaController(service);
        UUID id = UUID.randomUUID();
        IshikawaRequestDTO request = mock(IshikawaRequestDTO.class);
        IshikawaResponseDTO response = mock(IshikawaResponseDTO.class);
        when(service.buscarPorCiclo(1L)).thenReturn(List.of(response));
        when(service.buscar(id)).thenReturn(response);
        when(service.inserir(1L, request)).thenReturn(response);
        when(service.patch(id, Map.of())).thenReturn(response);

        assertEquals(HttpStatus.OK, controller.buscar(1L).getStatusCode());
        assertEquals(response, controller.buscar(id).getBody());
        assertEquals(HttpStatus.CREATED, controller.inserir(1L, request).getStatusCode());
        assertEquals(response, controller.patch(id, Map.of()).getBody());
        assertEquals(HttpStatus.NO_CONTENT, controller.excluir(id).getStatusCode());
        verify(service).excluir(id);
    }

    @Test
    void deveDelegarOperacoesDaLicaoAprendidaComStatusCorreto() {
        LicaoAprendidaService service = mock(LicaoAprendidaService.class);
        LicaoAprendidaController controller = new LicaoAprendidaController(service);
        UUID id = UUID.randomUUID();
        LicaoAprendidaRequestDTO request = mock(LicaoAprendidaRequestDTO.class);
        LicaoAprendidaResponseDTO response = mock(LicaoAprendidaResponseDTO.class);
        when(service.buscarPorCiclo(1L)).thenReturn(List.of(response));
        when(service.buscar(id)).thenReturn(response);
        when(service.inserir(1L, request)).thenReturn(response);
        when(service.patch(id, Map.of())).thenReturn(response);

        assertEquals(HttpStatus.OK, controller.buscar(1L).getStatusCode());
        assertEquals(response, controller.buscar(id).getBody());
        assertEquals(HttpStatus.CREATED, controller.inserir(1L, request).getStatusCode());
        assertEquals(response, controller.patch(id, Map.of()).getBody());
        assertEquals(HttpStatus.NO_CONTENT, controller.excluir(id).getStatusCode());
        verify(service).excluir(id);
    }

    @Test
    void deveDelegarOperacoesDoFormularioComStatusCorreto() {
        FormularioService service = mock(FormularioService.class);
        FormularioController controller = new FormularioController(service);
        UUID id = UUID.randomUUID();
        FormularioRequestDTO request = mock(FormularioRequestDTO.class);
        FormularioResponseDTO response = mock(FormularioResponseDTO.class);
        when(service.buscarPorCiclo(1L)).thenReturn(List.of(response));
        when(service.buscar(id)).thenReturn(response);
        when(service.inserir(1L, request)).thenReturn(response);
        when(service.patch(id, Map.of())).thenReturn(response);

        assertEquals(HttpStatus.OK, controller.buscar(1L).getStatusCode());
        assertEquals(response, controller.buscar(id).getBody());
        assertEquals(HttpStatus.CREATED, controller.inserir(1L, request).getStatusCode());
        assertEquals(response, controller.patch(id, Map.of()).getBody());
        assertEquals(HttpStatus.NO_CONTENT, controller.excluir(id).getStatusCode());
        verify(service).excluir(id);
    }

    @Test
    void deveDelegarOperacoesDaRespostaFormularioComStatusCorreto() {
        RespostaFormularioService service = mock(RespostaFormularioService.class);
        RespostaFormularioController controller = new RespostaFormularioController(service);
        UUID formulario = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        RespostaFormularioRequestDTO request = mock(RespostaFormularioRequestDTO.class);
        RespostaFormularioResponseDTO response = mock(RespostaFormularioResponseDTO.class);
        when(service.buscarPorFormulario(formulario)).thenReturn(List.of(response));
        when(service.buscar(id)).thenReturn(response);
        when(service.inserir(formulario, request)).thenReturn(response);
        when(service.patch(id, Map.of())).thenReturn(response);

        assertEquals(HttpStatus.OK, controller.buscar(formulario).getStatusCode());
        assertEquals(response, controller.buscarPorId(id).getBody());
        assertEquals(HttpStatus.CREATED, controller.inserir(formulario, request).getStatusCode());
        assertEquals(response, controller.patch(id, Map.of()).getBody());
        assertEquals(HttpStatus.NO_CONTENT, controller.excluir(id).getStatusCode());
        verify(service).excluir(id);
    }

    @Test
    void deveTraduzirStatusDoHealth() {
        HealthService service = mock(HealthService.class);
        HealthController controller = new HealthController(service);
        HealthResponseDTO up = new HealthResponseDTO(HealthStatus.UP, HealthStatus.UP, "ok", OffsetDateTime.now());
        HealthResponseDTO down = new HealthResponseDTO(HealthStatus.DOWN, HealthStatus.DOWN, "erro", OffsetDateTime.now());
        when(service.verificar()).thenReturn(up, down);
        assertEquals(HttpStatus.OK, controller.verificar().getStatusCode());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, controller.verificar().getStatusCode());
    }
}
