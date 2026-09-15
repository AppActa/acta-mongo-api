package br.com.acta.controller;

import br.com.acta.dto.formulario.FormularioRequestDTO;
import br.com.acta.dto.formulario.FormularioResponseDTO;
import br.com.acta.service.FormularioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
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
public class FormularioController {
    private final FormularioService service;

    @GetMapping("/ciclos/{idCiclo}/formularios")
    public ResponseEntity<List<FormularioResponseDTO>> buscar(@PathVariable @Positive Long idCiclo) {
        List<FormularioResponseDTO> dtos = service.buscarPorCiclo(idCiclo);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/formularios/{idFormulario}")
    public ResponseEntity<FormularioResponseDTO> buscar(@PathVariable UUID idFormulario) {
        FormularioResponseDTO dto = service.buscar(idFormulario);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/ciclos/{idCiclo}/formularios")
    public ResponseEntity<FormularioResponseDTO> inserir(@PathVariable @Positive Long idCiclo, @Valid @RequestBody FormularioRequestDTO dto) {
        FormularioResponseDTO formulario = service.inserir(idCiclo, dto);
        return ResponseEntity.status(201).body(formulario);
    }

    @PatchMapping("/formularios/{idFormulario}")
    public ResponseEntity<FormularioResponseDTO> patch(@PathVariable UUID idFormulario, @RequestBody Map<String, Object> campos) {
        FormularioResponseDTO dto = service.patch(idFormulario, campos);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/formularios/{idFormulario}")
    public ResponseEntity<Void> excluir(@PathVariable UUID idFormulario) {
        service.excluir(idFormulario);
        return ResponseEntity.noContent().build();
    }
}
