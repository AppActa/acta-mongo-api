package br.com.acta.controller;

import br.com.acta.dto.cinco_porques.CincoPorquesRequestDTO;
import br.com.acta.dto.cinco_porques.CincoPorquesResponseDTO;
import br.com.acta.service.CincoPorquesService;
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
public class CincoPorquesController {
    private final CincoPorquesService service;

    @GetMapping("/ishikawas/{idIshikawa}/cinco-porques")
    public ResponseEntity<List<CincoPorquesResponseDTO>> buscar(@PathVariable UUID idIshikawa) {
        List<CincoPorquesResponseDTO> dtos = service.buscarPorIshikawa(idIshikawa);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/cinco-porques/{idCincoPorques}")
    public ResponseEntity<CincoPorquesResponseDTO> buscarPorId(@PathVariable UUID idCincoPorques) {
        CincoPorquesResponseDTO dto = service.buscar(idCincoPorques);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/ishikawas/{idIshikawa}/cinco-porques")
    public ResponseEntity<CincoPorquesResponseDTO> inserir(@PathVariable UUID idIshikawa, @Valid @RequestBody CincoPorquesRequestDTO dto) {
        CincoPorquesResponseDTO cincoPorques = service.inserir(idIshikawa, dto);
        return ResponseEntity.status(201).body(cincoPorques);
    }

    @PatchMapping("/cinco-porques/{idCincoPorques}")
    public ResponseEntity<CincoPorquesResponseDTO> patch(@PathVariable UUID idCincoPorques, @RequestBody Map<String, Object> campos) {
        CincoPorquesResponseDTO dto = service.patch(idCincoPorques, campos);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/cinco-porques/{idCincoPorques}")
    public ResponseEntity<Void> excluir(@PathVariable UUID idCincoPorques) {
        service.excluir(idCincoPorques);
        return ResponseEntity.noContent().build();
    }
}
