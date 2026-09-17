package br.com.acta.controller;

import br.com.acta.dto.ishikawa.IshikawaRequestDTO;
import br.com.acta.dto.ishikawa.IshikawaResponseDTO;
import br.com.acta.service.IshikawaService;
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
public class IshikawaController {
    private final IshikawaService service;

    @GetMapping("/ciclos/{idCiclo}/ishikawas")
    public ResponseEntity<List<IshikawaResponseDTO>> buscar(@PathVariable @Positive Long idCiclo) {
        List<IshikawaResponseDTO> dtos = service.buscarPorCiclo(idCiclo);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/ishikawas/{idIshikawa}")
    public ResponseEntity<IshikawaResponseDTO> buscar(@PathVariable UUID idIshikawa) {
        IshikawaResponseDTO dto = service.buscar(idIshikawa);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/ciclos/{idCiclo}/ishikawas")
    public ResponseEntity<IshikawaResponseDTO> inserir(@PathVariable @Positive Long idCiclo, @Valid @RequestBody IshikawaRequestDTO dto) {
        IshikawaResponseDTO ishikawa = service.inserir(idCiclo, dto);
        return ResponseEntity.status(201).body(ishikawa);
    }

    @PatchMapping("/ishikawas/{idIshikawa}")
    public ResponseEntity<IshikawaResponseDTO> patch(@PathVariable UUID idIshikawa, @RequestBody Map<String, Object> campos) {
        IshikawaResponseDTO dto = service.patch(idIshikawa, campos);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/ishikawas/{idIshikawa}")
    public ResponseEntity<Void> excluir(@PathVariable UUID idIshikawa) {
        service.excluir(idIshikawa);
        return ResponseEntity.noContent().build();
    }
}
