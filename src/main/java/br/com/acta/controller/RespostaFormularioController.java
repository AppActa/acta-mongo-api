package br.com.acta.controller;

import br.com.acta.common.config.swagger.openapi.RespostaFormularioOpenapi;
import br.com.acta.dto.resposta_formulario.RespostaFormularioRequestDTO;
import br.com.acta.dto.resposta_formulario.RespostaFormularioResponseDTO;
import br.com.acta.service.RespostaFormularioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Validated
@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class RespostaFormularioController implements RespostaFormularioOpenapi {
    private final RespostaFormularioService service;

    @GetMapping("/formularios/{idFormulario}/respostas")
    @Override
    public ResponseEntity<List<RespostaFormularioResponseDTO>> buscar(@PathVariable UUID idFormulario) {
        List<RespostaFormularioResponseDTO> dtos = service.buscarPorFormulario(idFormulario);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/respostas-formulario/{idRespostaFormulario}")
    @Override
    public ResponseEntity<RespostaFormularioResponseDTO> buscarPorId(@PathVariable UUID idRespostaFormulario) {
        RespostaFormularioResponseDTO dto = service.buscar(idRespostaFormulario);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/formularios/{idFormulario}/respostas")
    @Override
    public ResponseEntity<RespostaFormularioResponseDTO> inserir(@PathVariable UUID idFormulario, @Valid @RequestBody RespostaFormularioRequestDTO dto) {
        RespostaFormularioResponseDTO respostaFormulario = service.inserir(idFormulario, dto);
        return ResponseEntity.status(201).body(respostaFormulario);
    }

    @PatchMapping("/respostas-formulario/{idRespostaFormulario}")
    @Override
    public ResponseEntity<RespostaFormularioResponseDTO> patch(@PathVariable UUID idRespostaFormulario, @RequestBody Map<String, Object> campos) {
        RespostaFormularioResponseDTO dto = service.patch(idRespostaFormulario, campos);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/respostas-formulario/{idRespostaFormulario}")
    @Override
    public ResponseEntity<Void> excluir(@PathVariable UUID idRespostaFormulario) {
        service.excluir(idRespostaFormulario);
        return ResponseEntity.noContent().build();
    }
}
